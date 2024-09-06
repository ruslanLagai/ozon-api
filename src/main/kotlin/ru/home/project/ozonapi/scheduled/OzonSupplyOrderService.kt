package ru.home.project.ozonapi.scheduled

import org.apache.commons.lang3.StringUtils
import org.openapitools.client.models.PlacementType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.supply.response.SupplyOrderItem
import ru.home.project.ozonapi.entity.FbsOrderEntity
import ru.home.project.ozonapi.entity.OzonSupplyEntity
import ru.home.project.ozonapi.exception.YandexException
import ru.home.project.ozonapi.repository.FbsOrderRepository
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.OzonService
import ru.home.project.ozonapi.service.YandexService
import ru.home.project.ozonapi.util.yandexInDeliveryStatuses
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *
 * По расписанию запускается, запрашивает поставки из озона, если поставка новая вычитает товары из остатков
 *
 * @author rlagay
 */
@Service
class OzonSupplyOrderService(
    val ozonService: OzonService,
    val yandexService: YandexService,
    val fbsRepository: FbsOrderRepository,
    val supplyRepository: OzonSupplyRepository,
    val stockRepository: StockRepository
) {

    private val log: Logger = LoggerFactory.getLogger(OzonSupplyOrderService::class.java)

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    @Scheduled(cron = "\${service.ozon.supply.cron}")
    fun checkOzonSupply() {
        ozonService.getSupplyOrders().forEach {
            val supplyEntity = supplyRepository.getOzonSupplyEntityByOrderId(it.orderId)
            if (supplyEntity == null) {
                subtractStocks(it)
                supplyRepository.save(OzonSupplyEntity(orderId = it.orderId, subtracted = true))
            } else if (!supplyEntity.subtracted) {
                subtractStocks(it)
                supplyRepository.updateByOrderId(it.orderId)
            } else {
                log.info("Supply order is already subtracted, orderId {}", supplyEntity.orderId)
            }
        }
    }

    @Scheduled(cron = "\${service.fbs.order.cron}")
    fun processYandexStocks() {
        val latestOrder = fbsRepository.findFirstByOrderByDateDesc()
        val latestDate = latestOrder?.date ?: LocalDate.now()
        val campaigns = yandexService.getCampaigns()
        val fbsCampaign = campaigns?.filter { it.placementType == PlacementType.FBS }?.map { it.id }
        val fbyCampaign = campaigns?.filter { it.placementType == PlacementType.FBY }?.map { it.id }
        subtractFbsOrders(fbsCampaign, latestDate)
        subtractFbySupply(fbyCampaign)
    }

    private fun subtractFbySupply(fbyCampaign: List<Long?>?) {
        kotlin.runCatching {
            if (fbyCampaign == null) {
                log.info("Skipping fbs stocks processing - no FBY campaign")
                return
            }
            if (fbyCampaign.size > 1) {
                log.warn("Found more than 1 fbs campaigns: {}", fbyCampaign.joinToString(separator = " "))
                return
            }

            yandexService.getStocksReport(from = LocalDate.now().minusDays(1), to = LocalDate.now(), campaignId = fbyCampaign[0]!!)
                .filter { it.value != 0 }
                .forEach {
                    val quantity = stockRepository.getByYandexArtikul(it.key)?.quantity ?: 0
                    if (quantity == 0) {
                        log.warn("Error subtracting fby order, stock will be < 0, artikul {}", it)
                    } else {
                        stockRepository.updateQuantityByYandexArtikul(it.key, quantity - it.value)
                    }
                }
        }.onFailure {
            when (it) {
                is YandexException -> log.error("Failed to subtract FBY stocks due to Yandex error", it)
                else -> log.error("Failed to subtract FBY stocks", it)
            }
        }.onSuccess { log.info("Successfully subtracted FBY orders") }
    }


    /**
     * Уменьшение остатков на кол-во fbs заказов
     */
    private fun subtractFbsOrders(fbsCampaign: List<Long?>?, latestDate: LocalDate) {
        val ordersToSave = ArrayList<FbsOrderEntity>()
        val stocksToSubtract = HashMap<String, Int>()
        kotlin.runCatching {
            if (fbsCampaign == null) {
                log.info("Skipping fbs stocks processing - no FBS campaign")
                return
            }
            if (fbsCampaign.size > 1) {
                log.warn("Found more than 1 fbs campaigns: {}", fbsCampaign.joinToString(separator = " "))
                return
            }

            val orders = yandexService.getOrders(
                from = latestDate, to = LocalDate.now(), statuses = yandexInDeliveryStatuses, campaignId = fbsCampaign[0]!!
            )

            orders.forEach {
                val existed = fbsRepository.getByNumberAndSubtracted(it.id!!.toString(), true)
                if (existed == null) {
                    it.items
                        ?.filter { item -> StringUtils.isNotBlank(item.shopSku) }
                        ?.forEach { item ->
                            val quantity = stockRepository.getByYandexArtikul(item.shopSku!!)?.quantity ?: 0
                            if (quantity == 0) {
                                log.warn("Error subtracting fbs order, stock will be < 0, artikul {}", item.shopSku)
                            } else {
                                item.count?.let { count ->
                                    val updatedQuantity =
                                        stocksToSubtract.computeIfAbsent(item.shopSku!!) { _ -> quantity } - count
                                    stocksToSubtract[item.shopSku!!] = updatedQuantity
                                }
                            }
                        }
                    ordersToSave.add(FbsOrderEntity(
                        number = it.id.toString(),
                        date = LocalDate.parse(it.creationDate, formatter),
                        subtracted = true)
                    )
                }
            }
        }.onFailure {
            when (it) {
                is YandexException -> log.error("Failed to subtract FBS stocks due to Yandex error", it)
                else -> log.error("Failed to subtract FBS stocks", it)
            }
        }.onSuccess {
            fbsRepository.saveAll(ordersToSave)
            stocksToSubtract.forEach { (k, v) -> stockRepository.updateQuantityByYandexArtikul(k, v) }
            log.info("Successfully subtracted FBS orders")
        }
    }

    private fun subtractStocks(supply: SupplyOrderItem) {
        ozonService.getSupplyItemsInOrder(supply.orderId)
            .forEach {
                stockRepository.getByArtikul(it.artikul)?.let { item ->
                    val quantity = item.quantity - it.quantity
                    stockRepository.updateQuantityByOzonId(item.ozonId, quantity)
                    if (quantity < 0) {
                        log.warn("Quantity in stock is < 0, artikul {}", it.artikul)
                    }
                }
            }
    }
}
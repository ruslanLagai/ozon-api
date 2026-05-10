package ru.home.project.ozonapi.scheduled

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.entity.OzonSupplyEntity
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.OzonService
import java.time.format.DateTimeFormatter

/**
 *
 * По расписанию запускается, запрашивает поставки из озона, если поставка новая вычитает товары из остатков
 *
 * @author rlagay
 */
@Service
class OzonSupplyOrderService(
    private val ozonService: OzonService,
    private val supplyRepository: OzonSupplyRepository,
    private val stockRepository: StockRepository
) {

    private val log: Logger = LoggerFactory.getLogger(OzonSupplyOrderService::class.java)

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

    @Scheduled(cron = "\${service.ozon.supply.cron}")
    fun checkOzonSupply() {
        val ordersToSubtract = ArrayList<Int>()
        ozonService.getSupplyOrders()
            .onEach {
                val supplyEntity = supplyRepository.getOzonSupplyEntityByOrderId(it)
                if (supplyEntity == null) {
                    ordersToSubtract.add(it)
                    supplyRepository.save(OzonSupplyEntity(orderId = it, subtracted = true))
                } else if (!supplyEntity.subtracted) {
                    supplyRepository.updateByOrderId(it)
                } else {
                    log.info("Supply order is already subtracted, orderId {}", supplyEntity.orderId)
                }
            }
        if (ordersToSubtract.isEmpty()) {
            log.info("Supply orders are already subtracted")
        }
        subtractStocks(ordersToSubtract)
    }

    private fun subtractStocks(orderIds: List<Int>) {
        ozonService.getSupplyItemsInOrder(orderIds)
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
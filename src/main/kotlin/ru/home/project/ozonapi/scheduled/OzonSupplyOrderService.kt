package ru.home.project.ozonapi.scheduled

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.home.project.ozonapi.dto.supply.response.SupplyOrderItem
import ru.home.project.ozonapi.entity.OzonSupplyEntity
import ru.home.project.ozonapi.repository.OzonSupplyRepository
import ru.home.project.ozonapi.repository.StockRepository
import ru.home.project.ozonapi.service.OzonService

/**
 *
 * По расписанию запускается, запрашивает поставки из озона, если поставка новая вычитает товары из остатков
 *
 * @author rlagay
 */
@Service
class OzonSupplyOrderService(
    val ozonService: OzonService,
    val supplyRepository: OzonSupplyRepository,
    val stockRepository: StockRepository
) {

    private val log: Logger = LoggerFactory.getLogger(OzonSupplyOrderService::class.java)

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
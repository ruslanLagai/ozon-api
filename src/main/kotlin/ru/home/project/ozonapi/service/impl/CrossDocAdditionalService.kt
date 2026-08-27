package ru.home.project.ozonapi.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.entity.CrossDocTransactionEntity
import ru.home.project.ozonapi.repository.CrossDocTransactionEntityRepository
import ru.home.project.ozonapi.repository.OzonSupplyOrderIdRepository
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import ru.home.project.ozonapi.service.OzonService
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

@Service
class CrossDocAdditionalService(
    private val ozonSupplyOrderIdRepository: OzonSupplyOrderIdRepository,
    private val ozonService: OzonService,
    private val crossDocTransactionEntityRepository: CrossDocTransactionEntityRepository
) : AdditionalServicesForCostPriceService {

    companion object {
        private val log = LoggerFactory.getLogger(CrossDocAdditionalService::class.java)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateCostPrice(orderId: Long, additionalServices: Double) {
        val ozonSupplyOrders = ozonSupplyOrderIdRepository.findByOrderId(orderId)
        if (ozonSupplyOrders.isEmpty()) {
            return
        }
        val bundleIds = ozonSupplyOrders.map { it.bundleId }.toSet()
        val productsInOrder = ozonService.getSupplyItemsByBundleIds(bundleIds)
        val quantity = productsInOrder.sumOf { it.quantity }
        val skusInOrder = productsInOrder.map { it.sku.toString() }.toSet()
        log.info("Для поставки с id '$orderId' найдено ${productsInOrder.size} товаров с sku в $skusInOrder и общей количеством $quantity")

        if (quantity == 0) {
            log.warn("Не удалось распределить cross-doc для поставки с id '$orderId': количество товаров равно 0")
            return
        }

        ozonSupplyOrders.forEach { orderIdEntity ->
            val chinaOrderEntity = orderIdEntity.chinaOrderEntity
            chinaOrderEntity.costPriceEntities
                .filter { skusInOrder.contains(it.ozonId) }
                .forEach {
                    it.crossDoc = BigDecimal.valueOf(it.crossDoc + abs(additionalServices) / quantity)
                        .setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
            }
        }
        val isNew = crossDocTransactionEntityRepository.findByOrderId(orderId.toString()).isEmpty

        if (isNew) {
            crossDocTransactionEntityRepository.save(
                CrossDocTransactionEntity(
                    orderId = orderId.toString()
                )
            )
        }
    }
}
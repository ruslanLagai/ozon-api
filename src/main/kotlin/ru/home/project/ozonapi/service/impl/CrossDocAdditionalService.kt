package ru.home.project.ozonapi.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.repository.OzonSupplyOrderIdRepository
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

@Service
class CrossDocAdditionalService(
    private val ozonSupplyOrderIdRepository: OzonSupplyOrderIdRepository
) : AdditionalServicesForCostPriceService {

    companion object {
        private val log = LoggerFactory.getLogger(CrossDocAdditionalService::class.java)
    }

    @Transactional
    override fun updateCostPrice(orderId: Long, additionalServices: Double) {
        val ozonSupplyOrders = ozonSupplyOrderIdRepository.findByOrderId(orderId)
        val quantity = ozonSupplyOrders.sumOf {
            orderIdEntity -> orderIdEntity.chinaOrderEntity.products.sumOf { it.quantity }
        }
        ozonSupplyOrders.forEach { orderIdEntity ->
            val chinaOrderEntity = orderIdEntity.chinaOrderEntity
            chinaOrderEntity.costPriceEntities.forEach {
                it.crossDoc = BigDecimal.valueOf(it.crossDoc + abs(additionalServices) / quantity)
                    .setScale(2, RoundingMode.HALF_UP)
                    .toDouble()
            }
        }
    }
}
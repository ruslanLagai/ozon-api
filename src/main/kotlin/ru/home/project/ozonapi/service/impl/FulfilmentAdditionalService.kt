package ru.home.project.ozonapi.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.exception.InvalidChineOrderException
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class FulfilmentAdditionalService(
    private val chinaOrdersRepository: ChinaOrdersRepository
) : AdditionalServicesForCostPriceService {

    @Transactional
    override fun updateCostPrice(orderId: Long, additionalServices: Double) {
        chinaOrdersRepository.findById(orderId).ifPresentOrElse( { order ->
            val quantity = order.products.sumOf { it.quantity }
            order.costPriceEntities.forEach {
                it.fulfilment = BigDecimal.valueOf(additionalServices / quantity).setScale(2, RoundingMode.HALF_UP).toDouble()
            }
        }, { throw InvalidChineOrderException("Поставка с id '${orderId}' не найдена") })
    }
}
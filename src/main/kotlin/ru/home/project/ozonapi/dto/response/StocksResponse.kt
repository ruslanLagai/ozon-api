package ru.home.project.ozonapi.dto.response

import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.model.Product

/**
 * @author rlagay
 */
data class StocksResponse(
    var stocksOnWayWorth: Double = 0.0,
    var stocksWorth: Double = 0.0,
    var deliveryWorth: Double = 0.0,
    var yandexDeliveryWorth: Double = 0.0,
    val products: Map<String, Product> = HashMap(),
    val orders: Set<ChinaOrderEntity> = HashSet(),
    val deliveries: Map<String, Product> = HashMap(),
    val yandexDeliveries: Map<String, Product> = HashMap(),
    var error: String = ""
)

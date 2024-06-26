package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.request.ProductRequest

/**
 * @author rlagay
 */
interface OrdersService {

    /**
     * Сохранить данные по поставке, не включая данные по доставке
     */
    fun saveNewOrder(supplier: String, stockWorthRub: Double, number: String?, products: List<ProductRequest>)

    /**
     * Добавление данных по доставке в БД, после того как груз прибыл, поставка считается завершенной и
     * при расчете используются остатки на соб складах вместо поставки
     */
    fun addDelivery(orderId: Long, deliveryCost: Double, mass: Double, volume: Double?)
}
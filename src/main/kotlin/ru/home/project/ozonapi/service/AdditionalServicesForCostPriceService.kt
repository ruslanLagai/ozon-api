package ru.home.project.ozonapi.service

interface AdditionalServicesForCostPriceService {

    /**
     * Обновляет себестоимость товара для заданного заказа, учитывая дополнительные услуги.
     */
    fun updateCostPrice(orderId: Long, additionalServices: Double)
}
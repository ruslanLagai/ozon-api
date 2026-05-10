package ru.home.project.ozonapi.service

interface CrossDocService {

    /**
     * Связывает поставки FBO с заказом из Китая для дальнейшего расчета себестоимости, учитывая дополнительные услуги.
     */
    fun linkWithOrders(orderId: Long)
}
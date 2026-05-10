package ru.home.project.ozonapi.service

/**
 * @author rlagay
 */
interface CostPriceService {

    /**
     * Получение себестоимости по FIFO
     */
    fun getFifoCostPrice(ozonId: String, artikul: String): Double

    /**
     * Установка себестоимости товара
     */
    fun setCostPrice(ozonId: String, artikul: String): Double

}
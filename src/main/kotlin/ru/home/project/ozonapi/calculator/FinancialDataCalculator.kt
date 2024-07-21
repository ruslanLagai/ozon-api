package ru.home.project.ozonapi.calculator

import org.openapitools.client.models.OrdersStatsOrderDTO
import ru.home.project.ozonapi.dto.finance.response.Transaction

/**
 * @author rlagay
 */
interface FinancialDataCalculator {

    /**
     * Расчет фин результата по транзакции
     */
    fun calculateRevenue(transaction: Transaction): Double

    /**
     * Расчет оборота по позиции
     */
    fun calculatePrice(transaction: Transaction): Double

    /**
     * Расчет комиссии
     */
    fun calculateCommission(transaction: Transaction): Double

    /**
     * Расчет логистики
     */
    fun calculateLogistic(transaction: Transaction): Double

    /**
     * Расчет последней мили
     */
    fun calculateLastMile(transaction: Transaction): Double

    /**
     * Расчет затрат по возвратам
     */
    fun calculateRefund(transaction: Transaction): Double

    /**
     * Расчет начисления по заказу Яндекс маркета
     */
    fun calculateYandexRevenue(order: OrdersStatsOrderDTO): Double

    /**
     * Расчет оборота по позиции
     */
    fun calculateYandexPrice(order: OrdersStatsOrderDTO): Double

    /**
     * Расчет комиссии
     */
    fun calculateYandexCommission(order: OrdersStatsOrderDTO): Double

    /**
     * Расчет логистики
     */
    fun calculateYandexDelivery(order: OrdersStatsOrderDTO): Double

    /**
     * Расчет рекламы
     */
    fun calculateYandexMarketing(order: OrdersStatsOrderDTO): Double

    /**
     * Расчет эквайринга
     */
    fun calculateYandexAcquiring(order: OrdersStatsOrderDTO): Double
}
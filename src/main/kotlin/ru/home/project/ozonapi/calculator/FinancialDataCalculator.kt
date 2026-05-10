package ru.home.project.ozonapi.calculator

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
}
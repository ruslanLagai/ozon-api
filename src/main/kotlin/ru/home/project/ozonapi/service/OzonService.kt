package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.finance.response.RefundData
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.stocks.response.StocksResultItem
import ru.home.project.ozonapi.dto.supply.response.SupplyItem
import ru.home.project.ozonapi.dto.supply.response.SupplyOrderItem
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
interface OzonService {

    /**
     * Получение транзакций за период
     */
    fun getTransaction(from : OffsetDateTime, to : OffsetDateTime, key: String): List<Transaction>

    /**
     * Получение транзакций по отправлению
     */
    fun getTransaction(postingNumber: String): List<Transaction>

    /**
     * Получение информации по возврату
     */
    fun getRefundData(postingNumber: String): RefundData?

    /**
     * Получение позиций в поставке
     */
    fun getSupplyItemsInOrder(orderId: Int): List<SupplyItem>

    /**
     * Получение списка поставок
     */
    fun getSupplyOrders(): List<SupplyOrderItem>

    /**
     * Получение остатков на складе озон FBO + FBS
     */
    fun getStockItems(cacheKey: String): List<StocksResultItem>
}
package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.delivery.Delivery
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import ru.home.project.ozonapi.dto.finance.response.RefundData
import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.dto.supply.response.SupplyBundleItem
import ru.home.project.ozonapi.model.Product
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
    fun getSupplyItemsInOrder(orderIds: List<Int>): List<SupplyBundleItem>

    /**
     * Получение списка поставок
     */
    fun getSupplyOrders(): List<Int>

    /**
     * Получение остатков на складе озон FBO + FBS
     */
    fun getStockItems(cacheKey: String): List<Product>

    /**
     * Получение отправлений по статусу
     */
    fun getDeliveryByStatus(status: DeliveryStatus): List<Delivery>
}
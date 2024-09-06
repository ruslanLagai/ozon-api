package ru.home.project.ozonapi.service

import org.openapitools.client.models.CampaignDTO
import org.openapitools.client.models.OrderDTO
import org.openapitools.client.models.OrderStatusType
import org.openapitools.client.models.OrdersStatsOrderDTO
import ru.home.project.ozonapi.dto.YandexReportResult
import ru.home.project.ozonapi.model.Product
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * @author rlagay
 */
interface YandexService {

    /**
     * Получение списка кампаний
     */
    fun getCampaigns() : List<CampaignDTO>?

    /**
     * Получение транзакций за период
     */
    fun getTransaction(from : LocalDate, to : LocalDate, key: String, statuses: Set<OrderStatusType>): List<OrdersStatsOrderDTO>


    /**
     * Получение заказов по кампании
     */
    fun getOrders(from: LocalDate, to: LocalDate, statuses: Set<OrderStatusType>, campaignId: Long): List<OrderDTO>

    /**
     * Получение отчета по услугам
     */
    fun getReport(from: LocalDate, to: LocalDate) : Pair<YandexReportResult, CompletableFuture<*>>


    /**
     * Получение отчета по движению товара на складах
     */
    fun getStocksReport(from: LocalDate, to: LocalDate, campaignId: Long) : HashMap<String, Int>

    /**
     * Получение остатков с Яндекс маркета
     */
    fun getStocks(cacheKey: String) : List<Product>
}
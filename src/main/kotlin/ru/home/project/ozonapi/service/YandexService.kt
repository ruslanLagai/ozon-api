package ru.home.project.ozonapi.service

import org.openapitools.client.models.OrdersStatsOrderDTO
import ru.home.project.ozonapi.dto.YandexReportResult
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

/**
 * @author rlagay
 */
interface YandexService {

    /**
     * Получение транзакций за период
     */
    fun getTransaction(from : LocalDate, to : LocalDate, key: String): List<OrdersStatsOrderDTO>

    /**
     * Получение отчета по услугам
     */
    fun getReport(from: LocalDate, to: LocalDate) : Pair<YandexReportResult, CompletableFuture<*>>
}
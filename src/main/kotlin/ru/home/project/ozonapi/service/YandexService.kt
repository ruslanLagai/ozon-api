package ru.home.project.ozonapi.service

import org.openapitools.client.models.OrdersStatsOrderDTO
import java.time.LocalDate

/**
 * @author rlagay
 */
interface YandexService {

    /**
     * Получение транзакций за период
     */
    fun getTransaction(from : LocalDate, to : LocalDate, key: String): List<OrdersStatsOrderDTO>

}
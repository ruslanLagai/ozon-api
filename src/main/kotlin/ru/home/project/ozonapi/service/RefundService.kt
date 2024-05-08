package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.response.RefundResponse
import ru.home.project.ozonapi.dto.response.RefundsByClusterResponse
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
interface RefundService {

    /**
     * Получение данных по возвратам по позиции
     */
    fun getRefundsForPeriod(from: OffsetDateTime, to: OffsetDateTime, name: String): RefundResponse

    /**
     * Получение данных по возвратам по кластеру
     */
    fun getRefundsForPeriodAndCluster(from: OffsetDateTime, to: OffsetDateTime, cluster: String, name: String): RefundsByClusterResponse
}
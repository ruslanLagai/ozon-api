package ru.home.project.ozonapi.service

import ru.home.project.ozonapi.dto.response.TotalRefundsResponse
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
interface TotalRefundsService {

    /**
     * Получение данных по всем возвратам
     */
    fun getRefundsData(from: OffsetDateTime, to: OffsetDateTime): TotalRefundsResponse

    /**
     * Получение данных по всем возвратам распределенные по кластерам
     */
    fun getRefundsDataByClusters(from: OffsetDateTime, to: OffsetDateTime): TotalRefundsResponse
}
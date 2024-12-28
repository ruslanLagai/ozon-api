package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

/**
 * @author rlagay
 */
data class AnalyticsDataRequest(
    @get:JsonProperty("date_from") val from: LocalDate,
    @get:JsonProperty("date_to") val to: LocalDate,
    @get:JsonProperty("metrics") val metrics: List<AnalyticMetric>,
    @get:JsonProperty("dimension") val dimension: List<AnalyticDimension> = listOf(),
    @get:JsonProperty("date_to") val sort: List<AnalyticSorting>?,
    @get:JsonProperty("limit") val limit: Int = 1000,
    @get:JsonProperty("offset") val offset: Int,
    @get:JsonProperty("filters") val filters: List<AnalyticFilter>? = listOf()

)

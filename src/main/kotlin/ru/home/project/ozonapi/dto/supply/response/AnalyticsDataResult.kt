package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class AnalyticsDataResult(
    @JsonProperty("totals") val totals: List<Int>?,
    @JsonProperty("data") val data: List<AnalyticsData>
)

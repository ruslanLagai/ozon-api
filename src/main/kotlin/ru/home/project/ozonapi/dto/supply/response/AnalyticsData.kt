package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class AnalyticsData(
    @JsonProperty("dimensions") val dimensions: List<AnalyticsDimension>,
    @JsonProperty("metrics") val metrics: List<Int>
)

package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class AnalyticsDataResp(
    @JsonProperty("result") val result: AnalyticsDataResult,
    @JsonProperty("timestamp") val timestamp: String
)

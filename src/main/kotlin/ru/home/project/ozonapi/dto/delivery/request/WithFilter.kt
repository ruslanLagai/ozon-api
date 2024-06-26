package ru.home.project.ozonapi.dto.delivery.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class WithFilter(
    @get:JsonProperty("analytics_data") val analyticDate: Boolean = false,
    @get:JsonProperty("financial_data") val financialData: Boolean = false
)

package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class AnalyticsDimension(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String
)

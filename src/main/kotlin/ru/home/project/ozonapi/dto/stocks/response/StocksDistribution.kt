package ru.home.project.ozonapi.dto.stocks.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class StocksDistribution(
    @JsonProperty("type") val type: String,
    @JsonProperty("present") val present: Int,
    @JsonProperty("reserved") val reserved: Int
)

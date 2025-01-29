package ru.home.project.ozonapi.dto.stocks.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class GetStocksRequest(
    @JsonProperty("limit") val limit: Int = 1000,
    @JsonProperty("cursor") val cursor: String = "",
    @JsonProperty("filter") val filter: StocksFilter
)

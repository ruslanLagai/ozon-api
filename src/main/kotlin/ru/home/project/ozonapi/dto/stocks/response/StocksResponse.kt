package ru.home.project.ozonapi.dto.stocks.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class StocksResponse(
    @JsonProperty("items") val items: List<StocksResultItem>,
    @JsonProperty("cursor") val cursor: String,
    @JsonProperty("total") val total: Int
)

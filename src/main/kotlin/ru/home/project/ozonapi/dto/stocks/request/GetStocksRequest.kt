package ru.home.project.ozonapi.dto.stocks.request

import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.finance.response.Transaction
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
data class GetStocksRequest(
    @JsonProperty("limit") val limit: Int = 100,
    @JsonProperty("last_id") val lastId: String = "",
    @JsonProperty("filter") val filter: StocksFilter
)

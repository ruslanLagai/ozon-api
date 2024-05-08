package ru.home.project.ozonapi.dto.stocks.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.finance.response.Transaction
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class StocksResult(
    @JsonProperty("items") val items: List<StocksResultItem>,
    @JsonProperty("last_id") val lastId: String,
    @JsonProperty("total") val total: Int
)

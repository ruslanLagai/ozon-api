package ru.home.project.ozonapi.dto.stocks.request

import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.finance.response.Transaction
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
data class StocksFilter(
    @JsonProperty("offer_id") val offerId: List<String> = ArrayList(),
    @JsonProperty("product_id") val productId: List<String> = ArrayList(),
    @JsonProperty("visibility") val visibility: String = "ALL"
)

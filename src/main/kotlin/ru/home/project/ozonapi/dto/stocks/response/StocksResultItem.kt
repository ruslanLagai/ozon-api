package ru.home.project.ozonapi.dto.stocks.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.finance.response.Transaction
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class StocksResultItem(
    @JsonProperty("product_id") val productId: String,
    @JsonProperty("offer_id") val offerId: String,
    @JsonProperty("stocks") val stocks: List<StocksDistribution>
)

package ru.home.project.ozonapi.dto.stocks.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.finance.response.Transaction
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetStocksResponse(
    @JsonProperty("result") val result: StocksResult
)

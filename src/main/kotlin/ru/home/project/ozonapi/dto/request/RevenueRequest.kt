package ru.home.project.ozonapi.dto.request

import ru.home.project.ozonapi.dto.finance.response.Transaction
import ru.home.project.ozonapi.entity.MarketType
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
data class RevenueRequest(
    val name: String?,
    val artikul: String? = null,
    val postingNumber: String? = null,
    val from: OffsetDateTime? = null,
    val to: OffsetDateTime? = null,
    val transactions: List<Transaction>? = null,
    val type: MarketType = MarketType.Ozon
)

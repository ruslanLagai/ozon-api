package ru.home.project.ozonapi.dto.response

/**
 * @author rlagay
 */
data class RefundsByNameResponse(
    val sku: String = "",
    val name: String,
    val refundsCount: Int,
    val refundsDelivered: Int,
    val refundsToBeDelivered: Int,
    val error: String? = null
)

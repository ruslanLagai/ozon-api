package ru.home.project.ozonapi.dto.response

import ru.home.project.ozonapi.dto.finance.response.RefundData

/**
 * @author rlagay
 */
data class RefundResponse(
    val name: String,
    val sku: String = "",
    var refundCount: Int = 0,
    var refundToBeDeliveredCount: Int = 0,
    var refundDeliveredCount: Int = 0,
    val refundsData: ArrayList<RefundData> = ArrayList(),
    val error: String? = null
)

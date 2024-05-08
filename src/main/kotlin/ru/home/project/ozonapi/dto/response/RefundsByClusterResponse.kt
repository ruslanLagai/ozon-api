package ru.home.project.ozonapi.dto.response

/**
 * @author rlagay
 */
data class RefundsByClusterResponse(
    val cluster: String = "",
    val sku: String = "",
    val name: String,
    var refundCount: Int = 0,
    var refundToBeDeliveredCount: Int = 0,
    var refundDeliveredCount: Int = 0,
    val data: ArrayList<RefundsByClusterData> = ArrayList(),
    val error: String? = null
)

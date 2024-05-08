package ru.home.project.ozonapi.dto.response

/**
 * @author rlagay
 */
data class TotalRefundsResponse(
    var totalRefundsCount: Int = 0,
    var totalRefundsDeliveredCount: Int = 0,
    var totalRefundsToBeDeliveredCount: Int = 0,
    val refundsData: ArrayList<RefundResponse> = ArrayList(),
    val refundsByNameData: ArrayList<RefundsByNameResponse> = ArrayList(),
    val refundsByClusterData: ArrayList<RefundsByClusterResponse> = ArrayList(),
    val error: String? = null
) {
    val itemByCluster = HashMap<String, TotalRefundsResponse>()
}

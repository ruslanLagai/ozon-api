package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersResp(
    @JsonProperty("order_ids") val supplyOrders: List<Int>,
    @JsonProperty("last_id") val lastOrderId: String?
)

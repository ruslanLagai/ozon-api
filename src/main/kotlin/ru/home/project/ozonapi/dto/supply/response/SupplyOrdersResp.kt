package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersResp(
    @JsonProperty("supply_order_id") val supplyOrders: List<Int>,
    @JsonProperty("last_supply_order_id") val lastOrderId: Int
)

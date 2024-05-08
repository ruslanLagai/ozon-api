package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersResp(
    @JsonProperty("supply_orders") val result: List<SupplyOrderItem>,
    @JsonProperty("total_supply_orders_count") val itemCount: Int,
    @JsonProperty("has_next") val hasNext: Boolean
)

package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyItemsRequest(
    @JsonProperty("page") val page: Int,
    @JsonProperty("page_size") val size: Int,
    @JsonProperty("supply_order_id") val orderId: Int
)

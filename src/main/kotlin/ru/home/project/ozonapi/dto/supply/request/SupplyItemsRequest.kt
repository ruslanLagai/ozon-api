package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyItemsRequest(
    @get:JsonProperty("page") val page: Int,
    @get:JsonProperty("page_size") val size: Int,
    @get:JsonProperty("supply_order_id") val orderId: Int
)

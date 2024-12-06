package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrderItemsRequest(
    @get:JsonProperty("order_ids") val orderIds: List<String>
)

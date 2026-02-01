package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersFilter(
    @get:JsonProperty("states") val states: List<SupplyState>,
    @get:JsonProperty("order_number_search") val orderNumber: String? = null,
    @get:JsonProperty("dropoff_warehouse_ids") val warehouseIds: List<String>? = null
)

package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersPaging(
    @get:JsonProperty("from_supply_order_id") val fromOrderId: Int? = null,
    @get:JsonProperty("limit") val limit: Int = 100
)

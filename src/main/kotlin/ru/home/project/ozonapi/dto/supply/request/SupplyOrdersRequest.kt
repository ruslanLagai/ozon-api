package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersRequest(
    @get:JsonProperty("last_id") val lastId: String? = null,
    @get:JsonProperty("sort_dir") val sortDir: String = "DESC",
    @get:JsonProperty("limit") val limit: Int = 100,
    @get:JsonProperty("sort_by") val sortBy: String = "ORDER_CREATION",
    @get:JsonProperty("filter") val filter: SupplyOrdersFilter
)

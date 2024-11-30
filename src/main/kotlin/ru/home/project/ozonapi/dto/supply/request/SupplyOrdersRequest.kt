package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersRequest(
    @get:JsonProperty("paging") val paging: SupplyOrdersPaging,
    @get:JsonProperty("filter") val filter: SupplyOrdersFilter
)

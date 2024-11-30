package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersFilter(
    @get:JsonProperty("states") val states: List<SupplyState>
)

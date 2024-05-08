package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyOrdersRequest(
    @JsonProperty("page") val page: Int,
    @JsonProperty("page_size") val size: Int,
    @JsonProperty("states") val states: List<SupplyState>
)

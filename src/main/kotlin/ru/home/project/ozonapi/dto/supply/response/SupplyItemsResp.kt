package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyItemsResp(
    @JsonProperty("items") val items: List<SupplyItem>,
    @JsonProperty("total_items_count") val itemCount: Int,
    @JsonProperty("has_next") val hasNext: Boolean
)

package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyBundlesResp(
    @JsonProperty("items") val items: List<SupplyBundleItem>,
    @JsonProperty("total_count") val itemCount: Int,
    @JsonProperty("has_next") val hasNext: Boolean,
    @JsonProperty("last_id") val lastId: String
)

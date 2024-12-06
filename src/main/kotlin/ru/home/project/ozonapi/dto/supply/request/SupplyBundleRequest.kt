package ru.home.project.ozonapi.dto.supply.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class SupplyBundleRequest(
    @get:JsonProperty("is_asc") val isAsc: Boolean = true,
    @get:JsonProperty("limit") val limit: Int = 100,
    @get:JsonProperty("bundle_ids") val bundleIds: List<String>
)

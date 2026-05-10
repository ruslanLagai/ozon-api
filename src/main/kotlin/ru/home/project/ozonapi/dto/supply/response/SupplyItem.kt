package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyItem(
    @param:JsonProperty("supply_id") val supplyId: Long,
    @param:JsonProperty("bundle_id") val bundleId : String
)
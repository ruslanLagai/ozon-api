package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyWaterhouse(
    @JsonProperty("warehouse_id") val warehouseId: Long,
    @JsonProperty("address") val address : String,
    @JsonProperty("name") val name: String
)
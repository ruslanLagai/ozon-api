package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyItem(
    @JsonProperty("icon_path") val iconPath: Int,
    @JsonProperty("sku") val orderNumber : Long,
    @JsonProperty("offer_id") val artikul: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("quantity") val quantity: Int
)
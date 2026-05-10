package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyBundleItem(
    @param:JsonProperty("icon_path") val iconPath: String?,
    @param:JsonProperty("sku") val sku : Long,
    @param:JsonProperty("offer_id") val artikul: String,
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("quantity") val quantity: Int
)
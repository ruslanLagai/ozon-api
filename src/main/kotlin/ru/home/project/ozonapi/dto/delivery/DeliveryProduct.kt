package ru.home.project.ozonapi.dto.delivery

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryProduct(
    @JsonProperty("sku") val sku: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("quantity") val quantity: Int,
    @JsonProperty("price") val price: Double,
    @JsonProperty("offer_id") val artikul: String,
    @JsonProperty("currency_code") val currency: String
)

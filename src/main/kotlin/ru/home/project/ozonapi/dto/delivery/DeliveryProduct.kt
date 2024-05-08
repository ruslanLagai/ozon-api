package ru.home.project.ozonapi.dto.delivery

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class DeliveryProduct(val sku: String, val name: String, val quantity: Int, val price: Double,
                           @JsonProperty("currency_code") val currency: String)

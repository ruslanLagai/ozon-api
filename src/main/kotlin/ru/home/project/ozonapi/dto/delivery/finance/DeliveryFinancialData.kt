package ru.home.project.ozonapi.dto.delivery.finance

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryFinancialData(@JsonProperty("products") val products: List<ProductFinancialData>,
                         @JsonProperty("cluster_to") val clusterTo: String,
                         @JsonProperty("cluster_from") val clusterFrom: String,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ProductFinancialData(@JsonProperty("commission_amount") val commission: Double,
                                    @JsonProperty("commission_percent") val commissionPercentage: Double,
                                    @JsonProperty("product_id") val productId: String,
                                    @JsonProperty("client_price") val clientPrice: Double,
                                    @JsonProperty("currency_code") val currency: Double,
                                    @JsonProperty("payout") val payout: Double,
                                    @JsonProperty("price") val price: Double,
    )
}



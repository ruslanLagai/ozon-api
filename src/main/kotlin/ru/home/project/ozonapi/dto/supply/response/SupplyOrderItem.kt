package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.supply.request.SupplyState
import java.time.LocalDate

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyOrderItem(
    @JsonProperty("supply_order_id") val orderId: Int,
    @JsonProperty("supply_order_number") val orderNumber : String,
    @JsonProperty("created_at") val createdAt: LocalDate,
    @JsonProperty("state") val state: SupplyState,
    @JsonProperty("total_quantity") val totalQuantity: Int,
    @JsonProperty("supply_warehouse") val supplyWarehouse: SupplyWaterhouse,
    @JsonProperty("product_bundle_ids") val productBundleIds: List<String>?
)
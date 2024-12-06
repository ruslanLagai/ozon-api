package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.supply.request.SupplyState
import java.time.LocalDate

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyOrderItem(
    @JsonProperty("supply_order_id") val orderId: Long,
    @JsonProperty("supply_order_number") val orderNumber : String,
    @JsonProperty("creation_date") @JsonFormat(pattern = "dd.MM.yyyy") val createdDate: LocalDate,
    @JsonProperty("state") val state: SupplyState,
    @JsonProperty("dropoff_warehouse_id") val warehouseId: String,
    @JsonProperty("supplies") val supplies: List<SupplyItem>?
)
package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.supply.request.SupplyState

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyOrderItem(
    @param:JsonProperty("order_id") val orderId: Long,
    @param:JsonProperty("order_number") val orderNumber : String,
    @param:JsonProperty("created_date") val createdDate: String,
    @param:JsonProperty("state") val state: SupplyState,
    @param:JsonProperty("supplies") val supplies: List<SupplyItem>?
)
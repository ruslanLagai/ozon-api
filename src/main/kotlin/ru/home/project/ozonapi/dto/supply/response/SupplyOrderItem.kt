package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.supply.request.SupplyState
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SupplyOrderItem(
    @JsonProperty("order_id") val orderId: Long,
    @JsonProperty("order_number") val orderNumber : String,
    @JsonProperty("created_date") val createdDate: ZonedDateTime,
    @JsonProperty("state") val state: SupplyState,
    @JsonProperty("supplies") val supplies: List<SupplyItem>?
)
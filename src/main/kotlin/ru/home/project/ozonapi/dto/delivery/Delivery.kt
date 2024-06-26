package ru.home.project.ozonapi.dto.delivery

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.delivery.finance.DeliveryFinancialData
import java.time.LocalDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Delivery(
    @JsonProperty("order_id") val orderId: String,
    @JsonProperty("order_number") val orderNumber: String,
    @JsonProperty("posting_number") val postingNumber: String,
    @JsonProperty("status") val status: DeliveryStatus,
    @JsonProperty("cancel_reason_id") val cancelId: Int?,
    @JsonProperty("created_at") val created: LocalDateTime,
    @JsonProperty("in_process_at") val inProcessFrom: LocalDateTime,
    @JsonProperty("products") val products: List<DeliveryProduct>,
    @JsonProperty("analytics_data") val analytic: DeliveryAnalytic?,
    @JsonProperty("financial_data") val financialData: DeliveryFinancialData?
)

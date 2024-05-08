package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

/**
 * @author rlagay
 */
data class RefundData(
    @JsonProperty("return_id") val returnId: String,
    @JsonProperty("id") val id : Long,
    @JsonProperty("sku") val sku: String,
    @JsonProperty("company_id") val companyId: String,
    @JsonProperty("posting_number") val postingNumber: String,
    @JsonProperty("accepted_from_customer_moment") val acceptedDate: ZonedDateTime?,
    @JsonProperty("return_reason_name") val reason: String,
    @JsonProperty("is_opened") val isOpened: Boolean,
    @JsonProperty("status_name") val statusName: String,
    @JsonProperty("returned_to_ozon_moment") val returnedDate: ZonedDateTime?,
    @JsonProperty("current_place_name") val currentPlaceName: String,
    @JsonProperty("dst_place_name") val destination: String
)
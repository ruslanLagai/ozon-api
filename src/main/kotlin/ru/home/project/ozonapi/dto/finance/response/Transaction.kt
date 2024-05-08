package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Transaction(@JsonProperty("operation_id") val operationId: String,
                       @JsonProperty("operation_type") val operationType: OperationType,
                       @JsonProperty("operation_date") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") val operationDate: LocalDateTime,
                       @JsonProperty("sale_commission") val saleCommission: Double,
                       @JsonProperty("amount") val income: Double,
                       @JsonProperty ("accruals_for_sale") val price: Double,
                       @JsonProperty("type") val type: TransactionType,
                       @JsonProperty("services") val services: List<ServiceItem>,
                       @JsonProperty("posting") val posting: Posting,
                       @JsonProperty("items") val items: List<Item>)

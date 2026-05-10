package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Transaction(@param:JsonProperty("operation_id") val operationId: String,
                       @param:JsonProperty("operation_type") val operationType: OperationType,
                       @param:JsonProperty("operation_date") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") val operationDate: LocalDateTime,
                       @param:JsonProperty("sale_commission") val saleCommission: Double,
                       @param:JsonProperty("amount") val income: Double,
                       @param:JsonProperty ("accruals_for_sale") val price: Double,
                       @param:JsonProperty("type") val type: TransactionType,
                       @param:JsonProperty("services") val services: List<ServiceItem>,
                       @param:JsonProperty("posting") val posting: Posting,
                       @param:JsonProperty("items") val items: List<Item>)

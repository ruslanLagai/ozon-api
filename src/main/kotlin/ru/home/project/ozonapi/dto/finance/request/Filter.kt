package ru.home.project.ozonapi.dto.finance.request

import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.finance.response.TransactionType

/**
 * @author rlagay
 */
data class Filter(@get:JsonProperty("operation_type") val operationType: List<String>? = ArrayList(),
                  @get:JsonProperty("posting_number") val postingNumber: String = "",
                  @get:JsonProperty("transaction_type") val transactionType: TransactionType? = TransactionType.all,
                  @get:JsonProperty("date") val date: Date? = null)

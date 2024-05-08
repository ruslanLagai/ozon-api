package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class TransactionsResult(@JsonProperty("operations") val operations: List<Transaction>,
                              @JsonProperty("row_count") val rowCount : Int,
                              @JsonProperty("page_count") val pageCount: Int)
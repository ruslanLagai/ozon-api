package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class TransactionsResult(
    @param:JsonProperty("operations") val operations: List<Transaction>,
    @param:JsonProperty("row_count") val rowCount : Int,
    @param:JsonProperty("page_count") val pageCount: Int)
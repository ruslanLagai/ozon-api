package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class TransactionsResp(@JsonProperty("result") val result: TransactionsResult)

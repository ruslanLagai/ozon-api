package ru.home.project.ozonapi.dto.finance.request

/**
 * @author rlagay
 */
data class TransactionsRequest(val filter: Filter, val page: Int = 1, val page_size: Int = 1000)

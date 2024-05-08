package ru.home.project.ozonapi.dto.finance.request

/**
 * @author rlagay
 */
data class RefundRequest(val filter: Filter, val last_id: Int = 0, val limit: Int = 10)

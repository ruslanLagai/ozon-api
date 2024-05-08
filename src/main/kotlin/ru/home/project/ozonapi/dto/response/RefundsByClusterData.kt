package ru.home.project.ozonapi.dto.response

import java.time.ZonedDateTime

/**
 * @author rlagay
 */
data class RefundsByClusterData(
    val name: String,
    val sku: String,
    val destination: String,
    val acceptedDate: ZonedDateTime?,
    val returnedDate: ZonedDateTime?
)

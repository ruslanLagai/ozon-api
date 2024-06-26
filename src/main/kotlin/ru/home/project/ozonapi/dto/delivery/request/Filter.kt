package ru.home.project.ozonapi.dto.delivery.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.delivery.DeliveryStatus
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
data class Filter(
    @get:JsonProperty("status") val status: DeliveryStatus,
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") val since: OffsetDateTime?,
    @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") val to: OffsetDateTime?
)

package ru.home.project.ozonapi.dto.finance.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
data class Date(@get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") val from: OffsetDateTime?,
                @get:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") val to: OffsetDateTime?)

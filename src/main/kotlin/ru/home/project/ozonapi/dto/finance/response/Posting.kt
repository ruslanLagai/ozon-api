package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Posting(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @JsonProperty("order_date") val date: LocalDateTime?,
                   @JsonProperty("posting_number") val postingNumber: String)

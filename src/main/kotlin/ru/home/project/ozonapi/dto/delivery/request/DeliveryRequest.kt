package ru.home.project.ozonapi.dto.delivery.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class DeliveryRequest(
    @get:JsonProperty("dir") val dir: String = "ASC",
    @get:JsonProperty("limit") val limit: Int = 1000,
    @get:JsonProperty("offset") val offset: Int = 0,
    @get:JsonProperty("translit") val translit: Boolean = true,
    @get:JsonProperty("with") val with: WithFilter = WithFilter(),
    @get:JsonProperty("filter") val filter: Filter
)

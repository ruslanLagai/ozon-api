package ru.home.project.ozonapi.dto.delivery.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ru.home.project.ozonapi.dto.delivery.Delivery

/**
 * @author rlagay
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliveryResponse(
    @JsonProperty("result")  val result: List<Delivery>
)

package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class ServiceItem(@JsonProperty("name")  val name: AdditionalServiceType?, @JsonProperty("price")  val price: Double?)

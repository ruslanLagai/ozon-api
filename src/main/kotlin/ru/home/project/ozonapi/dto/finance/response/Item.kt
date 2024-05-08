package ru.home.project.ozonapi.dto.finance.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class Item(@JsonProperty("name") val name: String, @JsonProperty("sku") val sku: String)

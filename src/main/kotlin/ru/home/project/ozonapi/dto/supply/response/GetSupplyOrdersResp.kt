package ru.home.project.ozonapi.dto.supply.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author rlagay
 */
data class GetSupplyOrdersResp(
    @JsonProperty("orders") val orders: List<SupplyOrderItem>,
)

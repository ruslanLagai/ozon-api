package ru.home.project.ozonapi.dto.response

import ru.home.project.ozonapi.dto.supply.request.AnalyticMetric

/**
 * @author rlagay
 */
data class AnalyticDataDto(
    val sku: String,
    val name: String,
    val yandexId: String? = null,
    val metrics: Map<AnalyticMetric, Int>
)

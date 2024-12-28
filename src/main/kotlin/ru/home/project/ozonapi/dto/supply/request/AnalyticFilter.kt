package ru.home.project.ozonapi.dto.supply.request

/**
 * EQ — равно,
 * GT — больше,
 * GTE — больше или равно,
 * LT — меньше,
 * LTE — меньше или равно.
 *
 * @author rlagay
 */
data class AnalyticFilter(val key: String, val op: String, val value: String)

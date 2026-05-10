package ru.home.project.ozonapi.model

/**
 * @author rlagay
 */
data class PositionFinanceData(
    val price: Double,
    val logistic: Double,
    val lastMile: Double,
    val commission: Double,
    val taxes: Double,
    val revenue: Double,
    val refund: Double,
    val marketing: Double = 0.0,
    val acquiring: Double = 0.0,
    val subsidies: Double = 0.0,
    val costPrice: Double
)

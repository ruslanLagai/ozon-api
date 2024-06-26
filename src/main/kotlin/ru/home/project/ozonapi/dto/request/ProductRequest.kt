package ru.home.project.ozonapi.dto.request

/**
 * @author rlagay
 */
data class ProductRequest(
    val artikul: String,
    val quantity: Int,
    val price: Double?
)

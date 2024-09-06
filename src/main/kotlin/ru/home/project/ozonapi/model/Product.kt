package ru.home.project.ozonapi.model

/**
 * @author rlagay
 */
data class Product(
    val price: Double = 0.0,
    val costPrice: Double = 0.0,
    val addCost: Double = 0.0,
    val name: String = "",
    val sku: String,
    val yandexArtikul : String? = "",
    val artikul: String,
    val fboStock: Int = 0,
    val fbsStock: Int = 0,
    var stockOnWay: Int = 0,
    var totalStock: Int
)

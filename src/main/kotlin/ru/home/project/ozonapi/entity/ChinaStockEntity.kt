package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
data class ChinaStockEntity(

    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long? = null,

    @Column(name = "name", nullable = true, unique = false) val name: String,

    @Column(name = "quantity", nullable = false) val quantity: Int,

    @Column(name = "ozon_id") val ozonId: String = "",

    @Column(name = "artikul") val artikul: String = "",

    @Column(name = "price_rub") val priceRub: Double = 0.0,

    @Column(name = "price") val price: Double = 0.0,

    @Column(name = "delivery_costs") val delivery: Double = 0.0,

    @Column(name = "delivery_usd") val deliveryUsd: Double = 0.0
)

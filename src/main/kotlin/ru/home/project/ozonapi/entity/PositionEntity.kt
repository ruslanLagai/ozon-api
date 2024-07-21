package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
data class PositionEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long? = null,

    @Column(name = "name", nullable = false, unique = true) val name: String,

    @Column(name = "cost_price", nullable = false) val costPrice: Double,

    @Column(name = "additional_cost") val additionalCost: Double,

    @Column(name = "ozon_id") val ozonId: String,

    @Column(name = "artikul") val artikul: String,

    @Column(name = "yandex_id") val yandexArtikul: String = ""
)

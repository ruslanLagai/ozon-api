package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
data class StockEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long? = null,

    @Column(name = "name", nullable = false, unique = true) val name: String,

    @Column(name = "quantity") val quantity: Int,

    @Column(name = "ozon_id", nullable = false, unique = true) val ozonId: String,

    @Column(name = "artikul", nullable = false, unique = true) val artikul: String,

    @Column(name = "yandex_artikul", nullable = true, unique = false) val yandexArtikul: String?,
)

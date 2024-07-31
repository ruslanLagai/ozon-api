package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
data class OzonSupplyEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long? = null,

    @Column(name = "orderId", nullable = false, unique = true) val orderId: Int,

    @Column(name = "subtracted") val subtracted: Boolean = false
)

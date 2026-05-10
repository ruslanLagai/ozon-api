package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Table(name = "ozon_supply_entity")
@Entity
class OzonSupplyEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long? = null,

    @Column(name = "order_id", nullable = false, unique = true) var orderId: Int,

    @Column(name = "subtracted") var subtracted: Boolean = false
)

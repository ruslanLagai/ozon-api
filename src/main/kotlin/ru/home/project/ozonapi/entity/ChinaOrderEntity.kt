package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * @author rlagay
 */
@Entity
data class ChinaOrderEntity(

    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long? = null,

    @Column(name = "supplier", nullable = false) val supplier: String,

    @Column(name = "isDelivered") var delivered: Boolean = false,

    @Column(name = "delivery_date") var deliveryDate: LocalDate? = null,

    @Column(name = "delivery_mass") var mass: Double = 0.0,

    @Column(name = "delivery_volume") var volume: Double = 0.0,

    @Column(name = "delivery_cost") var deliveryCost: Double = 0.0,

    @Column(name = "stock_cost") val stockCost: Double = 0.0,

    @Column(name = "number", nullable = true, unique = true) val number: String? = null,

    @OneToMany(fetch = FetchType.EAGER) @JoinColumn(name = "stock_entity_id") @Column(name = "products")
    val products: List<ChinaStockEntity>? = null
)

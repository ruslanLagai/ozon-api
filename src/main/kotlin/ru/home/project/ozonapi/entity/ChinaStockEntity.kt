package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
@Table(name = "china_stock_entity")
class ChinaStockEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long? = null,

    @Column(name = "name", nullable = true, unique = false) var name: String,

    @Column(name = "quantity", nullable = false) var quantity: Int,

    @Column(name = "ozon_id") var ozonId: String = "",

    @Column(name = "artikul") var artikul: String = "",

    @Column(name = "price_rub") var priceRub: Double = 0.0,

    @Column(name = "price") var price: Double = 0.0,

    @Column(name = "delivery_costs") var delivery: Double = 0.0,

    @Column(name = "delivery_usd") var deliveryUsd: Double = 0.0
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_entity_id", nullable = false)
    var chinaOrderEntity: ChinaOrderEntity? = null
}

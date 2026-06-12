package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * Связь FIFO-партии себестоимости с заказом поставки Ozon.
 */
@Entity
@Table(
    name = "ozon_supply_order_ids",
    indexes = [
        Index(name = "idx_ozonsupplyorderid_orderid", columnList = "ozon_supply_order_id"),
    ]
)
@NamedEntityGraph(
    name = "OzonSupplyOrderIdEntity.withChinaOrders",
    attributeNodes = [
        NamedAttributeNode("chinaOrderEntity")
    ]
)
class OzonSupplyOrderIdEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(name = "ozon_supply_order_id", nullable = false, unique = false)
    var orderId: Long,

    @Column(name = "ozon_supply_bundle_id", nullable = false, unique = false)
    var bundleId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "china_order_entity_id", nullable = false)
    var chinaOrderEntity: ChinaOrderEntity


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OzonSupplyOrderIdEntity

        if (id != other.id) return false
        if (orderId != other.orderId) return false
        if (bundleId != other.bundleId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = (31 * result + orderId).toInt()
        return result
    }
}


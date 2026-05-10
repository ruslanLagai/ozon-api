package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * @author rlagay
 */
@Entity
@Table(name = "china_order_entity")
@NamedEntityGraph(
    name = "ChinaOrderEntity.withOzonSupplyIds",
    attributeNodes = [
        NamedAttributeNode("ozonSupplyOrderIds")
    ]
)
class ChinaOrderEntity(

    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,

    @Column(name = "supplier", nullable = false) var supplier: String,

    @Column(name = "order_date", nullable = false) var orderDate: LocalDate,

    @Column(name = "is_delivered") var delivered: Boolean = false,

    @Column(name = "delivery_date") var deliveryDate: LocalDate? = null,

    @Column(name = "delivery_mass") var mass: Double = 0.0,

    @Column(name = "delivery_volume") var volume: Double = 0.0,

    @Column(name = "delivery_cost") var deliveryCost: Double = 0.0,

    @Column(name = "stock_cost") var stockCost: Double = 0.0,

    @Column(name = "number", nullable = true, unique = true) var number: String? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "chinaOrderEntity")
    var products: MutableList<ChinaStockEntity> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST], mappedBy = "chinaOrder")
    var costPriceEntities: MutableList<CostPriceEntity> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chinaOrderEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    var ozonSupplyOrderIds: MutableSet<OzonSupplyOrderIdEntity> = mutableSetOf(),


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChinaOrderEntity

        if (delivered != other.delivered) return false
        if (mass != other.mass) return false
        if (volume != other.volume) return false
        if (deliveryCost != other.deliveryCost) return false
        if (stockCost != other.stockCost) return false
        if (supplier != other.supplier) return false
        if (orderDate != other.orderDate) return false
        if (deliveryDate != other.deliveryDate) return false
        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        var result = delivered.hashCode()
        result = 31 * result + mass.hashCode()
        result = 31 * result + volume.hashCode()
        result = 31 * result + deliveryCost.hashCode()
        result = 31 * result + stockCost.hashCode()
        result = 31 * result + supplier.hashCode()
        result = 31 * result + orderDate.hashCode()
        result = 31 * result + (deliveryDate?.hashCode() ?: 0)
        result = 31 * result + (number?.hashCode() ?: 0)
        return result
    }
}

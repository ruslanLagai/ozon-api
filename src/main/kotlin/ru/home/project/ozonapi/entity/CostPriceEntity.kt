package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDate
import java.util.UUID

/**
 * @author rlagay
 */
@Entity
@Table(name = "cost_price_entity")
class CostPriceEntity(

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "left_quantity", nullable = false, unique = false) var leftQuantity: Int,

    @Column(name = "initial_quantity", nullable = false, unique = false) var initialQuantity: Int,

    @Column(name = "supply_date", nullable = false) var supplyDate: LocalDate,

    @Column(name = "cost_price", nullable = false) var costPrice: Double,

    @Column(name = "cross_doc") var crossDoc: Double,

    @Column(name = "fulfilment") var fulfilment: Double,

    @Column(name = "ozon_id", unique = true, nullable = false) var ozonId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false, updatable = true, insertable = true)
    var position: PositionEntity,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "china_order_id", nullable = false, updatable = true, insertable = true)
    var chinaOrder: ChinaOrderEntity,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fifoCostPrice")
    var transactions: MutableList<TransactionEntity>,

    @Version var version: Long? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as CostPriceEntity

        if (id != other.id) return false
        if (costPrice != other.costPrice) return false
        return true
    }

    override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

//    override fun toString(): String {
//        return "CostPriceEntity(leftQuantity=$leftQuantity, initialQuantity=$initialQuantity, supplyDate=$supplyDate, costPrice=$costPrice, crossDoc=$crossDoc, fulfilment=$fulfilment, ozonId='$ozonId', position=$position, chinaOrder=$chinaOrder, transactions=$transactions, version=$version, id=$id)"
//    }

}

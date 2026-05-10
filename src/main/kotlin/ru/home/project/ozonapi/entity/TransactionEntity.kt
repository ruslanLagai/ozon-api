package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import org.hibernate.Hibernate.getClass
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.proxy.HibernateProxy

/**
 * @author rlagay
 */
@Entity
@Table(name = "transaction_entity", indexes = [
    Index(name = "idx_transaction_entity_name", columnList = "operationId")
])
@DynamicUpdate
@DynamicInsert
class TransactionEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(name = "operation_id", nullable = false, unique = true) var operationId: String,

    @Column(name = "ozon_id", nullable = false) var ozonId: String,

    @Column(name = "is_failed", nullable = false) var isFailed: Boolean,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "cost_price_entity_id", nullable = false, unique = false)
    var fifoCostPrice: CostPriceEntity,

    @Version var version: Long? = null
) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null) return false
        val oEffectiveClass = if (o is HibernateProxy) o.hibernateLazyInitializer.persistentClass else o.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false

        o as TransactionEntity

        if (id != o.id) return false
        if (operationId != o.operationId) return false
        if (ozonId != o.ozonId) return false

        return true
    }

    override fun hashCode(): Int {
        return getClass(this).hashCode()
    }

}

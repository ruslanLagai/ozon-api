package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import org.hibernate.Hibernate.getClass
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

/**
 * @author rlagay
 */
@Entity
@Table(name = "failed_cost_price_entity")
@DynamicUpdate
@DynamicInsert
class FailedCostPriceTransactionEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = null,

    @Column(name = "operation_id", nullable = false, unique = true) val operationId: String,

    @Column(name = "quantity", nullable = false) val quantity: Int,

    @Column(name = "ozon_id", nullable = false) val ozonId: String,

    @Column(name = "fifo_cost_price") val operationDate: LocalDateTime,

    @Version val version: Long? = null
) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null) return false
        val oEffectiveClass = if (o is HibernateProxy) o.hibernateLazyInitializer.persistentClass else o.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false

        o as FailedCostPriceTransactionEntity

        if (id != o.id) return false
        if (operationId != o.operationId) return false
        if (ozonId != o.ozonId) return false

        return true
    }

    override fun hashCode(): Int {
        return getClass(this).hashCode()
    }

}

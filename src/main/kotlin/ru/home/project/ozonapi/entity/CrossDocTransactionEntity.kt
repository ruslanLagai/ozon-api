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
@Table(name = "cross_doc_transaction_entity", indexes = [
    Index(name = "cross_doc_transaction_id", columnList = "orderId")
])
@DynamicUpdate
@DynamicInsert
class CrossDocTransactionEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(name = "order_id", nullable = false, unique = true) var orderId: String,

    @Version var version: Long? = null
) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null) return false
        val oEffectiveClass = if (o is HibernateProxy) o.hibernateLazyInitializer.persistentClass else o.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false

        o as CrossDocTransactionEntity

        if (id != o.id) return false
        if (orderId != o.orderId) return false

        return true
    }

    override fun hashCode(): Int {
        return getClass(this).hashCode()
    }

}

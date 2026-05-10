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
@Table(name = "position_entity", indexes = [
    Index(name = "idx_positionentity_name", columnList = "name, cost_price")
])
@DynamicUpdate
@DynamicInsert
class PositionEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pos_sequence_generator")
//    @SequenceGenerator(
//        name = "pos_sequence_generator",
//        sequenceName = "position_entity_seq", // Name of the DB sequence
//        allocationSize = 10 // Fetch 50 IDs at a time
//    )
    var id: Long? = null,

    @Column(name = "name", nullable = false, unique = true) var name: String,

    @Column(name = "cost_price", nullable = false) var costPrice: Double,

    @Column(name = "additional_cost") var additionalCost: Double,

    @Column(name = "ozon_id") var ozonId: String,

    @Column(name = "artikul") var artikul: String,

    @Column(name = "yandex_id") var yandexArtikul: String = "",

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "position")
    var costPriceEntity: MutableList<CostPriceEntity> = mutableListOf(),

    @Version var version: Long? = null) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null) return false
        val oEffectiveClass = if (o is HibernateProxy) o.getHibernateLazyInitializer().getPersistentClass() else o.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) (this as HibernateProxy).getHibernateLazyInitializer().getPersistentClass() else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false

        o as PositionEntity

        if (id != o.id) return false
        if (name != o.name) return false
        if (costPrice != o.costPrice) return false
        if (additionalCost != o.additionalCost) return false
        if (ozonId != o.ozonId) return false
        if (artikul != o.artikul) return false
        if (yandexArtikul != o.yandexArtikul) return false

        return true
    }

    override fun hashCode(): Int {
        return if (this is HibernateProxy) {
            (this as HibernateProxy).getHibernateLazyInitializer().getPersistentClass().hashCode()
        } else {
            getClass(this).hashCode()
        }
    }

//    override fun toString(): String {
//        return "PositionEntity(id=$id, name='$name', costPrice=$costPrice, additionalCost=$additionalCost, ozonId='$ozonId', artikul='$artikul', yandexArtikul='$yandexArtikul')"
//    }
}

package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
@Table(name = "stock_entity")
class StockEntity(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long? = null,

    @Column(name = "name", nullable = false, unique = true) var name: String,

    @Column(name = "quantity") var quantity: Int,

    @Column(name = "ozon_id", nullable = false, unique = true) var ozonId: String,

    @Column(name = "artikul", nullable = false, unique = true) var artikul: String,

    @Column(name = "yandex_artikul", nullable = true, unique = false) var yandexArtikul: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StockEntity

        if (id != other.id) return false
        if (quantity != other.quantity) return false
        if (name != other.name) return false
        if (ozonId != other.ozonId) return false
        if (artikul != other.artikul) return false
        if (yandexArtikul != other.yandexArtikul) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity
        result = 31 * result + name.hashCode()
        result = 31 * result + ozonId.hashCode()
        result = 31 * result + artikul.hashCode()
        result = 31 * result + (yandexArtikul?.hashCode() ?: 0)
        return result
    }
}

package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.entity.StockEntity

/**
 * @author rlagay
 */
interface StockRepository: JpaRepository<StockEntity, Long> {

    fun getByOzonId(ozonId: String): StockEntity?

    fun getByArtikul(artikul: String): StockEntity?

    @Transactional
    @Modifying
    @Query("update StockEntity entity set entity.quantity = ?2 where entity.ozonId = ?1")
    fun updateQuantityByOzonId(ozonId: String, quantity: Int)


}
package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.entity.PositionEntity

/**
 * @author rlagay
 */
interface PositionRepository: JpaRepository<PositionEntity, Long> {

    fun getPositionEntityByName(name: String): PositionEntity?

    fun getPositionEntityByArtikul(artikul: String): PositionEntity?

    @Transactional
    @Modifying
    @Query("update PositionEntity position set position.costPrice = ?2, position.additionalCost = ?3 where position.artikul = ?1")
    fun updateByArtikul(artikul: String, costPrice: Double, addCosts: Double)

    @Transactional
    @Modifying
    @Query("update PositionEntity position set position.costPrice = ?2 where position.artikul = ?1")
    fun updateCostPriceByArtikul(artikul: String, costPrice: Double)

    @Transactional
    @Modifying
    @Query("update PositionEntity position set position.additionalCost = ?2 where position.artikul = ?1")
    fun updateAddCostsByArtikul(artikul: String, addCosts: Double)
}
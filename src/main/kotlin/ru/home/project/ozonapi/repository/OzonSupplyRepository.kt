package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.entity.OzonSupplyEntity

/**
 * @author rlagay
 */
interface OzonSupplyRepository: JpaRepository<OzonSupplyEntity, Long> {

    fun getOzonSupplyEntityByOrderId(orderId: Int): OzonSupplyEntity?

    @Transactional
    @Modifying
    @Query("update OzonSupplyEntity e set e.subtracted = true where e.orderId = ?1")
    fun updateByOrderId(orderId: Int)


}
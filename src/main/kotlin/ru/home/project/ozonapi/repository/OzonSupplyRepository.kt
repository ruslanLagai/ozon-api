package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.OzonSupplyEntity

/**
 * @author rlagay
 */
interface OzonSupplyRepository: JpaRepository<OzonSupplyEntity, Long> {

    fun getOzonSupplyEntityByOrderId(orderId: Int): OzonSupplyEntity?

}
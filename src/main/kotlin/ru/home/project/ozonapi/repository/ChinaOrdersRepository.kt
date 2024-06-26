package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.ChinaOrderEntity

/**
 * @author rlagay
 */
interface ChinaOrdersRepository: JpaRepository<ChinaOrderEntity, Long> {

    fun getChinaOrderEntityByDelivered(state: Boolean): Set<ChinaOrderEntity>

}
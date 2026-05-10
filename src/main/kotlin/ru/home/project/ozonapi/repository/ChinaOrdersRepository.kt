package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import java.util.Optional

/**
 * @author rlagay
 */
interface ChinaOrdersRepository: JpaRepository<ChinaOrderEntity, Long> {

    @EntityGraph("ChinaOrderEntity.withOzonSupplyIds")
    fun getChinaOrderEntityByDelivered(state: Boolean): Set<ChinaOrderEntity>

    @EntityGraph("ChinaOrderEntity.withOzonSupplyIds")
    @Query("select coe from ChinaOrderEntity coe where coe.id = :id")
    fun findByIdWithOzonSupplyIds(id: Long): Optional<ChinaOrderEntity>
}
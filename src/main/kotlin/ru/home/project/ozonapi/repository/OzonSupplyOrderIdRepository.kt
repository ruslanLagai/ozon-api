package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import java.util.*

interface OzonSupplyOrderIdRepository : JpaRepository<OzonSupplyOrderIdEntity, Long> {

    @EntityGraph("OzonSupplyOrderIdEntity.withChinaOrders")
    fun findByOrderId(orderId: Long): List<OzonSupplyOrderIdEntity>

}


package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import java.time.LocalDate

class ChinaOrdersRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: ChinaOrdersRepository

    @Test
    fun `should return only delivered orders with supply ids`() {
        repository.save(chinaOrder(delivered = true, number = "delivered-1", supplyIds = setOf(101L, 102L)))
        repository.save(chinaOrder(delivered = false, number = "not-delivered", supplyIds = setOf(201L)))

        val deliveredOrders = repository.getChinaOrderEntityByDelivered(true)

        assertEquals(1, deliveredOrders.size)
        val found = deliveredOrders.first()
        assertEquals("delivered-1", found.number)
        assertEquals(setOf(101L, 102L), found.ozonSupplyOrderIds.map { it.orderId }.toSet())
    }

    @Test
    fun `should find order by id with ozon supply ids`() {
        val saved = repository.save(chinaOrder(delivered = true, number = "find-me", supplyIds = setOf(777L)))

        val found = repository.findByIdWithOzonSupplyIds(saved.id!!).orElseThrow()

        assertEquals(saved.id, found.id)
        assertTrue(found.ozonSupplyOrderIds.any { it.orderId == 777L })
    }

    private fun chinaOrder(
        delivered: Boolean,
        number: String,
        supplyIds: Set<Long>
    ): ChinaOrderEntity {
        val order = ChinaOrderEntity(
            supplier = "supplier-$number",
            orderDate = LocalDate.now(),
            delivered = delivered,
            stockCost = 1000.0,
            number = number,
            ozonSupplyOrderIds = mutableSetOf()
        )
        supplyIds.forEach { supplyId ->
            order.ozonSupplyOrderIds.add(OzonSupplyOrderIdEntity(orderId = supplyId, chinaOrderEntity = order))
        }
        return order
    }
}


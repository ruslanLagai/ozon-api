package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.home.project.ozonapi.entity.ChinaOrderEntity
import ru.home.project.ozonapi.entity.OzonSupplyOrderIdEntity
import java.time.LocalDate

class OzonSupplyOrderIdRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: OzonSupplyOrderIdRepository

    @Autowired
    private lateinit var chinaOrdersRepository: ChinaOrdersRepository

    @Test
    fun `should find supply order id with china order`() {
        val order = ChinaOrderEntity(
            supplier = "supplier-1",
            orderDate = LocalDate.now(),
            stockCost = 1000.0,
            ozonSupplyOrderIds = mutableSetOf()
        )
        order.ozonSupplyOrderIds.add(OzonSupplyOrderIdEntity(orderId = 555L, chinaOrderEntity = order))
        chinaOrdersRepository.save(order)

        val found = repository.findByOrderId(555L).orElseThrow()

        assertEquals(555L, found.orderId)
        assertEquals("supplier-1", found.chinaOrderEntity.supplier)
        assertTrue(found.chinaOrderEntity.ozonSupplyOrderIds.any { it.orderId == 555L })
    }
}

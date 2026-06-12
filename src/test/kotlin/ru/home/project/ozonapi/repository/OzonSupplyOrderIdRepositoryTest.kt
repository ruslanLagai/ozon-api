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
        order.ozonSupplyOrderIds.add(OzonSupplyOrderIdEntity(orderId = 555L, chinaOrderEntity = order, bundleId = "bundle-1"))
        chinaOrdersRepository.save(order)

        val found = repository.findByOrderId(555L)

        assertEquals(555L, found[0].orderId)
        assertEquals("supplier-1", found[0].chinaOrderEntity.supplier)
        assertTrue(found[0].chinaOrderEntity.ozonSupplyOrderIds.any { it.orderId == 555L })
        assertTrue(found[0].chinaOrderEntity.ozonSupplyOrderIds.any { it.bundleId == "bundle-1" })
    }
}

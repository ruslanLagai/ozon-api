package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import ru.home.project.ozonapi.entity.PositionEntity
import java.time.LocalDate
import java.util.UUID

/**
 * JPA smoke tests for [CostPriceRepository].
 */
class CostPriceRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: CostPriceRepository

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun shouldReturnZeroCountOnEmptyTable() {
        assertEquals(0, repository.count())
    }

    @Test
    fun `should save and find cost price by id`() {
        persistChinaOrder(id = 1L, supplier = "supplier-1")

        val position = positionRepository.save(
            PositionEntity(
                name = "position-1",
                costPrice = 100.0,
                additionalCost = 5.0,
                ozonId = "ozon-position-1",
                artikul = "art-1"
            )
        )

        val costPriceId = UUID.randomUUID()
        persistCostPrice(
            id = costPriceId,
            positionId = position.id!!,
            chinaOrderId = 1L,
            ozonId = "cost-art-1"
        )

        val found = repository.findById(costPriceId).orElseThrow()
        assertNotNull(found.id)
        assertEquals(costPriceId, found.id)
        assertEquals(10, found.leftQuantity)
        assertEquals("cost-art-1", found.ozonId)
    }

    private fun persistChinaOrder(id: Long, supplier: String) {
        testEntityManager.entityManager.createNativeQuery(
            """
                insert into china_order_entity
                    (id, supplier, order_date, is_delivered, delivery_cost, delivery_mass, delivery_volume, stock_cost, number)
                values
                    (:id, :supplier, :orderDate, false, 0, 0, 0, 1000.0, null)
            """.trimIndent()
        )
            .setParameter("id", id)
            .setParameter("supplier", supplier)
            .setParameter("orderDate", LocalDate.now())
            .executeUpdate()
    }

    private fun persistCostPrice(id: UUID, positionId: Long, chinaOrderId: Long, ozonId: String) {
        testEntityManager.entityManager.createNativeQuery(
            """
                insert into cost_price_entity
                    (id, left_quantity, initial_quantity, supply_date, cost_price, cross_doc, fulfilment, ozon_id, position_id, china_order_id, version)
                values
                    (:id, 10, 10, :supplyDate, 125.0, 3.5, 1.0, :ozonId, :positionId, :chinaOrderId, 0)
            """.trimIndent()
        )
            .setParameter("id", id)
            .setParameter("supplyDate", LocalDate.now())
            .setParameter("ozonId", ozonId)
            .setParameter("positionId", positionId)
            .setParameter("chinaOrderId", chinaOrderId)
            .executeUpdate()
    }
}

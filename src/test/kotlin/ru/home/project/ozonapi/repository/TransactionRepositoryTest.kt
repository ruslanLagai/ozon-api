package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import ru.home.project.ozonapi.entity.PositionEntity
import java.time.LocalDate
import java.util.UUID

class TransactionRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: TransactionRepository

    @Autowired
    private lateinit var costPriceRepository: CostPriceRepository

    @Autowired
    private lateinit var positionRepository: PositionRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun shouldReturnZeroCountOnEmptyTable() {
        assertEquals(0, repository.count())
    }

    @Test
    fun `should save and find transaction by id`() {
        persistChinaOrder(id = 1L, supplier = "supplier-transaction")

        val position = positionRepository.save(
            PositionEntity(
                name = "position-transaction",
                costPrice = 200.0,
                additionalCost = 10.0,
                ozonId = "ozon-position-transaction",
                artikul = "art-transaction"
            )
        )

        val costPriceId = UUID.randomUUID()
        persistCostPrice(
            id = costPriceId,
            positionId = position.id!!,
            chinaOrderId = 1L,
            ozonId = "cost-art-transaction"
        )

        persistTransaction(
            id = 1L,
            costPriceId = costPriceId,
            operationId = "tx-1",
            ozonId = "ozon-tx-1"
        )

        val found = repository.findById(1L).orElseThrow()
        assertNotNull(found.id)
        assertEquals(1L, found.id)
        assertEquals("tx-1", found.operationId)
        assertEquals("ozon-tx-1", found.ozonId)
        assertEquals(false, found.isFailed)
        assertEquals(costPriceId, found.fifoCostPrice.id)
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
                    (:id, 3, 3, :supplyDate, 210.0, 5.0, 1.0, :ozonId, :positionId, :chinaOrderId, 0)
            """.trimIndent()
        )
            .setParameter("id", id)
            .setParameter("supplyDate", LocalDate.now())
            .setParameter("ozonId", ozonId)
            .setParameter("positionId", positionId)
            .setParameter("chinaOrderId", chinaOrderId)
            .executeUpdate()
    }

    private fun persistTransaction(id: Long, costPriceId: UUID, operationId: String, ozonId: String) {
        testEntityManager.entityManager.createNativeQuery(
            """
                insert into transaction_entity
                    (id, operation_id, ozon_id, is_failed, cost_price_entity_id, version)
                values
                    (:id, :operationId, :ozonId, false, :costPriceId, 0)
            """.trimIndent()
        )
            .setParameter("id", id)
            .setParameter("operationId", operationId)
            .setParameter("ozonId", ozonId)
            .setParameter("costPriceId", costPriceId)
            .executeUpdate()
    }
}

package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.home.project.ozonapi.entity.FailedCostPriceTransactionEntity
import java.time.LocalDateTime

/**
 * JPA tests for [FailedCostPriceTransactionRepository] using embedded test database.
 */
class FailedCostPriceTransactionRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: FailedCostPriceTransactionRepository

    @Test
    fun `should save and find entity by id`() {
        val entity = FailedCostPriceTransactionEntity(
            operationId = "operation-1",
            quantity = 5,
            ozonId = "ozon-123",
            operationDate = LocalDateTime.now().withNano(0)
        )

        val saved = repository.save(entity)

        assertNotNull(saved.id)

        val found = repository.findById(saved.id!!).orElseThrow()

        assertEquals(saved.id, found.id)
        assertEquals("operation-1", found.operationId)
        assertEquals(5, found.quantity)
        assertEquals("ozon-123", found.ozonId)
    }
}

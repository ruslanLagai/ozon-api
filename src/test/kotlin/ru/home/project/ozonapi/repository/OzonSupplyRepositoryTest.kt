package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import ru.home.project.ozonapi.entity.OzonSupplyEntity

class OzonSupplyRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: OzonSupplyRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `should find by order id`() {
        repository.save(OzonSupplyEntity(orderId = 1001, subtracted = false))

        val found = repository.getOzonSupplyEntityByOrderId(1001)

        assertEquals(1001, found?.orderId)
        assertEquals(false, found?.subtracted)
    }

    @Test
    fun `should update subtracted flag by order id`() {
        val saved = repository.save(OzonSupplyEntity(orderId = 1002, subtracted = false))

        repository.updateByOrderId(1002)
        testEntityManager.entityManager.flush()
        testEntityManager.entityManager.clear()

        val updated = repository.findById(saved.id!!).orElseThrow()
        assertTrue(updated.subtracted)
    }
}

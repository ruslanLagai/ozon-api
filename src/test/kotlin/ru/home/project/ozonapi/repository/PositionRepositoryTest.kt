package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import ru.home.project.ozonapi.entity.PositionEntity

class PositionRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: PositionRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `should find position by name artikul and ozon id`() {
        repository.save(positionEntity(name = "pos-1", artikul = "art-1", ozonId = "ozon-1"))

        assertEquals("pos-1", repository.getPositionEntityByName("pos-1")?.name)
        assertEquals("art-1", repository.getPositionEntityByArtikul("art-1")?.artikul)
        assertEquals("ozon-1", repository.getPositionEntityByOzonId("ozon-1").ozonId)
    }

    @Test
    fun `should update cost price and additional cost by artikul`() {
        repository.save(positionEntity(name = "pos-2", artikul = "art-2", ozonId = "ozon-2"))

        repository.updateByArtikul("art-2", 250.0, 30.0)
        testEntityManager.entityManager.flush()
        testEntityManager.entityManager.clear()

        val updated = repository.getPositionEntityByArtikul("art-2")!!
        assertEquals(250.0, updated.costPrice)
        assertEquals(30.0, updated.additionalCost)
    }

    @Test
    fun `should update individual fields by artikul`() {
        repository.save(positionEntity(name = "pos-3", artikul = "art-3", ozonId = "ozon-3"))

        repository.updateCostPriceByArtikul("art-3", 300.0)
        repository.updateAddCostsByArtikul("art-3", 15.5)
        testEntityManager.entityManager.flush()
        testEntityManager.entityManager.clear()

        val updated = repository.getPositionEntityByArtikul("art-3")!!
        assertEquals(300.0, updated.costPrice)
        assertEquals(15.5, updated.additionalCost)
    }

    private fun positionEntity(name: String, artikul: String, ozonId: String) = PositionEntity(
        name = name,
        costPrice = 100.0,
        additionalCost = 10.0,
        ozonId = ozonId,
        artikul = artikul,
        yandexArtikul = "ya-$artikul"
    )
}

package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import ru.home.project.ozonapi.entity.StockEntity

class StockRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: StockRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `should find stock by ozon id artikul and yandex artikul`() {
        repository.save(stockEntity(name = "stock-1", ozonId = "ozon-1", artikul = "art-1", yandexArtikul = "ya-1"))

        assertEquals("ozon-1", repository.getByOzonId("ozon-1")?.ozonId)
        assertEquals("art-1", repository.getByArtikul("art-1")?.artikul)
        assertEquals("ya-1", repository.getByYandexArtikul("ya-1")?.yandexArtikul)
    }

    @Test
    fun `should update quantity by ozon id`() {
        repository.save(stockEntity(name = "stock-2", ozonId = "ozon-2", artikul = "art-2", yandexArtikul = "ya-2"))

        repository.updateQuantityByOzonId("ozon-2", 25)
        testEntityManager.entityManager.flush()
        testEntityManager.entityManager.clear()

        val updated = repository.getByOzonId("ozon-2")!!
        assertEquals(25, updated.quantity)
    }

    @Test
    fun `should update quantity by yandex artikul`() {
        repository.save(stockEntity(name = "stock-3", ozonId = "ozon-3", artikul = "art-3", yandexArtikul = "ya-3"))

        repository.updateQuantityByYandexArtikul("ya-3", 40)
        testEntityManager.entityManager.flush()
        testEntityManager.entityManager.clear()

        val updated = repository.getByYandexArtikul("ya-3")!!
        assertEquals(10, updated.quantity)
    }

    private fun stockEntity(name: String, ozonId: String, artikul: String, yandexArtikul: String?) = StockEntity(
        name = name,
        quantity = 10,
        ozonId = ozonId,
        artikul = artikul,
        yandexArtikul = yandexArtikul
    )
}

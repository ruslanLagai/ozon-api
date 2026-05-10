package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.home.project.ozonapi.entity.FbsOrderEntity
import java.time.LocalDate

class FbsOrderRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: FbsOrderRepository

    @Test
    fun `should find order by number and subtracted flag`() {
        repository.save(FbsOrderEntity(number = "FBS-1", date = LocalDate.of(2024, 1, 1), subtracted = false))

        val found = repository.getByNumberAndSubtracted("FBS-1", false)
        val notFound = repository.getByNumberAndSubtracted("FBS-1", true)

        assertEquals("FBS-1", found?.number)
        assertNull(notFound)
    }

    @Test
    fun `should find latest order by date`() {
        repository.save(FbsOrderEntity(number = "FBS-old", date = LocalDate.of(2024, 1, 1), subtracted = false))
        repository.save(FbsOrderEntity(number = "FBS-new", date = LocalDate.of(2024, 2, 1), subtracted = true))

        val found = repository.findFirstByOrderByDateDesc()

        assertEquals("FBS-new", found?.number)
    }
}


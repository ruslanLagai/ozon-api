package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import ru.home.project.ozonapi.entity.TelegramUserEntity

class TelegramUserRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: TelegramUserRepository

    @Test
    fun `should find user by username`() {
        repository.save(TelegramUserEntity(id = null, username = "octopus", name = "Octo User"))

        val found = repository.getByUsername("octopus")
        val notFound = repository.getByUsername("missing")

        assertEquals("Octo User", found?.name)
        assertNull(notFound)
    }
}

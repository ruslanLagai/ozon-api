package ru.home.project.ozonapi.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import java.time.OffsetDateTime

class TelegramChatRepositoryTest : AbstractRepositoryTest() {

    @Autowired
    private lateinit var repository: TelegramChatRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Test
    fun `should find chat by chat id state and action`() {
        repository.save(
            TelegramChatEntity(
                id = null,
                chatId = 100L,
                positionName = "position-1",
                state = true,
                action = ActionType.Revenue
            )
        )

        val found = repository.getByChatIdAndStateAndAction(100L, true, ActionType.Revenue)

        assertEquals("position-1", found?.positionName)
    }

    @Test
    fun `should update state position and dates by chat id`() {
        val saved = repository.save(
            TelegramChatEntity(
                id = null,
                chatId = 101L,
                positionName = "initial",
                state = true,
                action = ActionType.AddPosition
            )
        )
        val from = OffsetDateTime.now().minusDays(1).withNano(0)
        val to = OffsetDateTime.now().withNano(0)

        repository.updatePositionByChatIdAnAndAction(101L, true, "updated")
        repository.updateDateByChatId(101L, from, to)
        repository.updateStateByChatIdAndAction(101L, false, ActionType.AddPosition)
        testEntityManager.entityManager.flush()
        testEntityManager.entityManager.clear()

        val updated = repository.findById(saved.id!!).orElseThrow()
        assertEquals("updated", updated.positionName)
        assertEquals(from, updated.from)
        assertEquals(to, updated.to)
        assertFalse(updated.state)
    }
}

package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
interface TelegramChatRepository: JpaRepository<TelegramChatEntity, Long> {

    fun getByChatIdAndState(chatId: Long, state: Boolean): TelegramChatEntity?

    fun getByChatIdAndStateAndAction(chatId: Long, state: Boolean, action: ActionType): TelegramChatEntity?

    fun deleteByChatId(chatId: Long)

    @Transactional
    @Modifying
    @Query("update TelegramChatEntity entity set entity.state = ?2 where entity.id = ?1")
    fun updateStateById(id: Long, state: Boolean)

    @Transactional
    @Modifying
    @Query("update TelegramChatEntity entity set entity.state = ?2 where entity.chatId = ?1 and entity.action = ?3")
    fun updateStateByChatIdAndAction(id: Long, state: Boolean, action: ActionType)

    @Transactional
    @Modifying
    @Query("update TelegramChatEntity entity set entity.positionName = ?2 where entity.chatId = ?1")
    fun updateNameByChatId(chatId: Long, name: String)

    @Transactional
    @Modifying
    @Query("update TelegramChatEntity entity set entity.to = ?2 where entity.chatId = ?1")
    fun updateToByChatId(chatId: Long, to: OffsetDateTime)

    @Transactional
    @Modifying
    @Query("update TelegramChatEntity entity set entity.to = ?3, entity.from = ?2 where entity.chatId = ?1")
    fun updateDateByChatId(chatId: Long, from: OffsetDateTime, to: OffsetDateTime)
}
package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.TelegramUserEntity

/**
 * @author rlagay
 */
interface TelegramUserRepository: JpaRepository<TelegramUserEntity, Long> {

    fun getByUsername(username: String): TelegramUserEntity?
}
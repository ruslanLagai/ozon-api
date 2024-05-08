package ru.home.project.ozonapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import ru.home.project.ozonapi.telegram.TelegramBot


/**
 *
 * VTB Group. Do not reproduce without permission in writing.
 * Copyright (c) $today.year VTB Group. All rights reserved.
 *
 * @author rlagay
 */
@Configuration
class TelegramConfig(
    val telegramBot: TelegramBot
) {

    @EventListener(value = [ContextRefreshedEvent::class])
    @Throws(TelegramApiException::class)
    fun init() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        try {
            telegramBotsApi.registerBot(telegramBot)
        } catch (e: TelegramApiException) {
        }
    }
}
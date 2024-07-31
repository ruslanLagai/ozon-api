package ru.home.project.ozonapi.telegram

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.repository.TelegramUserRepository
import ru.home.project.ozonapi.telegram.text.TextInputProcessor
import java.util.*

/**
 * @author rlagay
 */
@Component
class TelegramBot(
    @Value("\${telegram.bot.token}") token: String,
    val inputProcessors: List<TextInputProcessor>,
    val telegramUserRepository: TelegramUserRepository,
    val telegramChatRepository: TelegramChatRepository
) : TelegramLongPollingBot(token) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(TelegramBot::class.java)
    }

    override fun getBotUsername(): String {
        return "HouseMouseBot"
    }

    override fun onUpdateReceived(update: Update) {
        val chatId = update.message.chatId
        var message: SendMessage?

        try {
            val username = update.message.from.userName
            if (username == null) {
                message = SendMessage()
                message.chatId = update.message?.chatId.toString()
                message.text = "Доступ запрещен"
                execute(message)
                return
            }
            val isAllowed = Optional.ofNullable(telegramUserRepository.getByUsername(username)).isPresent

            if (isAllowed) {
                val text = update.message?.text

                if (text == null) {
                    message = SendMessage()
                    message.chatId = update.message?.chatId.toString()
                    message.text = "Отсутствует команда"
                    execute(message)
                    return
                }

                message = inputProcessors.map { it.processInput(text, update) }.firstOrNull { it != null }

                if (message == null) {
                    log.error("Empty message")
                    message = SendMessage()
                    message.text = "Не удалось обработать сообщение"
                }

                val isNeedToBeSplit = message.text.length > 4090
                if (isNeedToBeSplit) {
                    val messages = splitMessage(message)
                    messages.forEach { execute(it) }
                } else {
                    message.chatId = update.message?.chatId.toString()
                    execute(message)
                }
            } else {
                message = SendMessage()
                message.chatId = update.message?.chatId.toString()
                message.text = "Доступ запрещен"
                execute(message)
            }
        } catch (e: IncorrectResultSizeDataAccessException) {
            telegramChatRepository.updateStateByChatId(chatId)
            message = SendMessage()
            message.chatId = update.message?.chatId.toString()
            message.text = "Не удалось обработать сообщение из-за незавершенных сессий. Попробуйте снова."
            execute(message)
        }
    }

    private fun splitMessage(message: SendMessage): List<SendMessage> {
        val messageList = ArrayList<SendMessage>()
        val chatId = message.chatId

        val texts = message.text.chunked(4090)
        var buffer = ""

        for (text: String in texts) {
            val msg = SendMessage()
            msg.chatId = chatId
            msg.text = buffer + text.substringBeforeLast("\uD83D\uDCDD")
            buffer = text.substringAfterLast("\uD83D\uDCDD")
            messageList.add(msg)
            if (texts.indexOf(text) == (texts.size - 1) &&  (msg.text + buffer).length < 4090) {
                msg.text += buffer
            } else if (texts.indexOf(text) == (texts.size - 1)) {
                val last = SendMessage()
                last.chatId = chatId
                last.text = buffer
                messageList.add(last)
            }
        }
        return messageList
    }

//    override fun getBotPath(): String {
//        return "HouseMouseBot"
//    }
}
package ru.home.project.ozonapi.telegram.text

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.util.*

/**
 * @author rlagay
 */
@Component
class RefundsTypeInputProcessor(
    val telegramChatRepository: TelegramChatRepository
): TextInputProcessor {


    companion object {
        val log: Logger = LoggerFactory.getLogger(RefundsTypeInputProcessor::class.java)
    }

    override fun processInput(input: String, update: Update): SendMessage? {
        val isRefundTypeInput = refundsStatistics == input || refundsByCluster == input
        if (isRefundTypeInput) {
            try {
                telegramChatRepository.save(
                    TelegramChatEntity(chatId = update.message.chatId, positionName = input.trim(),
                        action = ActionType.Refund)
                )

                val keyBoardRows = ArrayList<KeyboardRow>()
                val keyboardRow1 = KeyboardRow()
                keyboardRow1.add(lastDayDate)
                keyboardRow1.add(lastTwoDaysDate)
                keyBoardRows.add(keyboardRow1)

                val keyboardRow2 = KeyboardRow()
                keyboardRow2.add(forCurrentWeek)
                keyboardRow2.add(forCurrentMonth)
                keyBoardRows.add(keyboardRow2)

                val replyKeyboardMarkup = ReplyKeyboardMarkup()
                replyKeyboardMarkup.apply {
                    selective = true
                    resizeKeyboard = true
                    oneTimeKeyboard = true
                    keyboard = keyBoardRows
                }

                val msg = SendMessage()
                msg.text = "Введите период, за который хотите увидеть статистику по возвратам. Формат: 21.10.2023-21.11.2023."
                msg.replyMarkup = replyKeyboardMarkup
                msg.chatId = update.message?.chatId.toString()
                return msg
            } catch (e: Exception) {
                log.error(e.message, e)
                val chatId = update.message!!.chatId
                telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.Refund)
                throw e
            }
        }
        return null
    }
}
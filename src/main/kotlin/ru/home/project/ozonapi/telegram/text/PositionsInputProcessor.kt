package ru.home.project.ozonapi.telegram.text

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.event.DbMigratedEvent
import ru.home.project.ozonapi.event.PositionAddedEvent
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.util.*

/**
 * @author rlagay
 */
@Component
class PositionsInputProcessor(
    val positionRepository: PositionRepository,
    val telegramChatRepository: TelegramChatRepository
): TextInputProcessor {

    private val positionNames = mutableSetOf<String>()

    @EventListener
    fun init(event: DbMigratedEvent) {
        val positions = positionRepository.findAll()
        positions.forEach { positionNames.add(it.name) }
        positionNames.add(allItems)
    }

    @EventListener
    fun addPosition(event: PositionAddedEvent) {
        positionNames.add(event.name)
    }

    override fun processInput(input: String, update: Update): SendMessage? {
        val isPositionInput = positionNames.contains(input.trim())
        if (isPositionInput) {
            telegramChatRepository.save(
                TelegramChatEntity(chatId = update.message.chatId, positionName = input.trim(),
                action = ActionType.Revenue)
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
            msg.text = "Введите период, за который хотите посчитать маржинальность. Формат: 21.10.2023-21.11.2023."
            msg.replyMarkup = replyKeyboardMarkup
            msg.chatId = update.message?.chatId.toString()
            return msg
        }
        return null
    }
}
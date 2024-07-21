package ru.home.project.ozonapi.telegram.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.repository.PositionRepository

/**
 * @author rlagay
 */
@Component
class PositionsCmdProcessor(
    val positionRepository: PositionRepository
): CmdProcessor {

    override fun processCmd(command: String, update: Update): SendMessage? {

        val positions = positionRepository.findAll()

        val keyBoardRows = ArrayList<KeyboardRow>()
        val allItemsRaw = KeyboardRow()
        keyBoardRows.add(allItemsRaw)

        positions.forEach {
            val keyboardRow = KeyboardRow()
            keyboardRow.add(it.name)
            keyBoardRows.add(keyboardRow)
        }

        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        replyKeyboardMarkup.apply {
            selective = false
            resizeKeyboard = true
            oneTimeKeyboard = true
            isPersistent = true
            keyboard = keyBoardRows
        }

        val msg = SendMessage()
        msg.replyMarkup = replyKeyboardMarkup
        msg.chatId = update.message?.chatId.toString()
        msg.text = "Доступные товары"
        return msg
    }
}
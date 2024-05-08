package ru.home.project.ozonapi.telegram.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.util.allItems

/**
 * @author rlagay
 */
@Component
class CalculationsCmdProcessor(
    val positionRepository: PositionRepository
): CmdProcessor {

    override fun processCmd(command: String, update: Update): SendMessage? {

        val keyBoardRows = ArrayList<KeyboardRow>()
        val positions = positionRepository.findAll()

        val allItemsRaw = KeyboardRow()
        allItemsRaw.add(allItems)
        keyBoardRows.add(allItemsRaw)

        positions.forEach {
            val keyboardRow = KeyboardRow()
            keyboardRow.add(it.name)
            keyBoardRows.add(keyboardRow)
        }

        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        replyKeyboardMarkup.apply {
            selective = true
            resizeKeyboard = true
            oneTimeKeyboard = true
            isPersistent = true
            keyboard = keyBoardRows
        }

        val msg = SendMessage()
        msg.replyMarkup = replyKeyboardMarkup
        msg.chatId = update.message?.chatId.toString()
        msg.text = "Выберите товар для расчета"
        return msg
    }
}
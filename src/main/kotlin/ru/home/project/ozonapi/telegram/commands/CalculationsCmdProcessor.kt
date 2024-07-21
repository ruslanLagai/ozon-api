package ru.home.project.ozonapi.telegram.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.util.ozon
import ru.home.project.ozonapi.util.yandex

/**
 * @author rlagay
 */
@Component
class CalculationsCmdProcessor: CmdProcessor {

    override fun processCmd(command: String, update: Update): SendMessage? {

        val keyBoardRows = ArrayList<KeyboardRow>()

        val yandexRow = KeyboardRow()
        yandexRow.add(yandex)
        val ozonRow = KeyboardRow()
        ozonRow.add(ozon)

        keyBoardRows.add(ozonRow)
        keyBoardRows.add(yandexRow)

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
        msg.text = "Выберите магазин"
        return msg
    }
}
package ru.home.project.ozonapi.telegram.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.util.allItems
import ru.home.project.ozonapi.util.refundsByCluster
import ru.home.project.ozonapi.util.refundsStatistics

/**
 * @author rlagay
 */
@Component
class RefundsCmdProcessor(
    val positionRepository: PositionRepository
): CmdProcessor {

    override fun processCmd(command: String, update: Update): SendMessage? {

        val keyBoardRows = ArrayList<KeyboardRow>()

        val allItemsRaw = KeyboardRow()
        allItemsRaw.add(refundsStatistics)
        keyBoardRows.add(allItemsRaw)

        val keyboardRow = KeyboardRow()
        keyboardRow.add(refundsByCluster)
        keyBoardRows.add(keyboardRow)


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
        msg.text = "Выберите тип статистики по возвратам"
        return msg
    }
}
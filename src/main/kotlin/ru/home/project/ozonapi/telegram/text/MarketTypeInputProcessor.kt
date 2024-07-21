package ru.home.project.ozonapi.telegram.text

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.MarketType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.OrdersService
import ru.home.project.ozonapi.util.allItems
import ru.home.project.ozonapi.util.ozon
import ru.home.project.ozonapi.util.yandex

/**
 * @author rlagay
 */
@Component
class MarketTypeInputProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val positionRepository: PositionRepository
): TextInputProcessor {

    private val errorMsg = "Добавьте данные по поставке в формате: \n" +
            "<стоимость доставки>,<масса груза>,<вес груза>"
    companion object {
        val log: Logger = LoggerFactory.getLogger(MarketTypeInputProcessor::class.java)
    }

    override fun processInput(input: String, update: Update): SendMessage? {
        if (input != yandex && input != ozon) {
            return null
        }

        val market = when (input) {
            yandex -> MarketType.Yandex
            ozon -> MarketType.Ozon
            else -> MarketType.Ozon
        }

        telegramChatRepository.save(
            TelegramChatEntity(chatId = update.message.chatId, positionName = input.trim(),
                action = ActionType.Revenue, market = market)
        )

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
            selective = false
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
package ru.home.project.ozonapi.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository

/**
 * @author rlagay
 */
@Component
class DeliveryDataCmdProcessor(
    val chinaOrdersRepository: ChinaOrdersRepository,
    val telegramChatRepository: TelegramChatRepository
): CmdProcessor {

    private val log: Logger = LoggerFactory.getLogger(DeliveryDataCmdProcessor::class.java)

    override fun processCmd(command: String, update: Update): SendMessage? {
        val msg = SendMessage()
        msg.chatId = update.message?.chatId.toString()
        msg.text = "Выберите заказ"
        val keyBoardRows = ArrayList<KeyboardRow>()
        try {
            chinaOrdersRepository.getChinaOrderEntityByDelivered(false)
                .forEach {
                    val allItemsRaw = KeyboardRow()
                    val builder = StringBuilder().append(it.supplier)
                    if (it.number != null) {
                        builder.append(" №${it.number}")
                    }
                    builder.append(" от " + it.orderDate + " на сумму " + it.stockCost)
                    allItemsRaw.add(builder.toString())
                    keyBoardRows.add(allItemsRaw)
                }
            val replyKeyboardMarkup = ReplyKeyboardMarkup()
            replyKeyboardMarkup.apply {
                selective = true
                resizeKeyboard = true
                oneTimeKeyboard = true
                isPersistent = true
                keyboard = keyBoardRows
            }
            if (keyBoardRows.isEmpty()) {
                msg.text = "Нет заказов в пути"
            } else {
                telegramChatRepository.save(
                    TelegramChatEntity(chatId = update.message.chatId, positionName = "", action = ActionType.AddDelivery, state = true)
                )
            }
            msg.replyMarkup = replyKeyboardMarkup

        } catch (e: Exception) {
            msg.text = "Не удалось получить список поставок"
            log.error("Failed to retrieve orders", e)
        }
        return msg
    }
}
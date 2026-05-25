package ru.home.project.ozonapi.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
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
class AddFulfilmentCmdProcessor(
    val chinaOrdersRepository: ChinaOrdersRepository,
    val telegramChatRepository: TelegramChatRepository,
): CmdProcessor {

    private val log: Logger = LoggerFactory.getLogger(AddFulfilmentCmdProcessor::class.java)

    @Transactional
    override fun processCmd(command: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val sb = StringBuilder().append("Выберите поставку\n")
        msg.text = ""
        msg.chatId = update.message?.chatId.toString()
        val keyBoardRows = ArrayList<KeyboardRow>()

        try {
            val orders = chinaOrdersRepository.getChinaOrderEntityByDeliveredOrderByDeliveryDateDesc(true)
            orders.stream()
                .limit(5)
                .forEach {
                    val row = KeyboardRow()
                    val builder = StringBuilder().append(it.supplier)
                    if (!it.number.isNullOrEmpty()) {
                        builder.append(" №${it.number}")
                    }
                    builder.append(" от " + it.orderDate + " на сумму " + it.stockCost)
                    val text = builder.toString()
                    row.add(text)
                    keyBoardRows.add(row)
                }
            val replyKeyboardMarkup = ReplyKeyboardMarkup()
            replyKeyboardMarkup.apply {
                selective = false
                resizeKeyboard = true
                oneTimeKeyboard = true
                isPersistent = true
                keyboard = keyBoardRows
            }
            msg.text = if (orders.isEmpty()) { "Поставки отсутствуют" } else { sb.toString() }
            msg.replyMarkup = replyKeyboardMarkup
            telegramChatRepository.save(
                TelegramChatEntity(
                    chatId = update.message.chatId,
                    positionName = "",
                    action = ActionType.AddFulfilment,
                    state = true)
            )
        } catch (e: Exception) {
            msg.text = "Не удалось получить список поставок"
            log.error("Failed to retrieve orders", e)
        }
        return msg
    }
}
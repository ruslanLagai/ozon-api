package ru.home.project.ozonapi.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.TelegramChatEntity
import ru.home.project.ozonapi.repository.TelegramChatRepository

/**
 * @author rlagay
 */
@Component
class OrderCmdProcessor(
    val telegramChatRepository: TelegramChatRepository,
): CmdProcessor {

    private val log: Logger = LoggerFactory.getLogger(OrderCmdProcessor::class.java)

    override fun processCmd(command: String, update: Update): SendMessage? {
        val msg = SendMessage()
        msg.chatId = update.message?.chatId.toString()
        try {
            telegramChatRepository.save(
                TelegramChatEntity(chatId = update.message.chatId, positionName = "", deliveryId = 0, action = ActionType.AddOrder, state = true)
            )

            msg.text = "Добавьте данные по поставке в формате: \n" +
                    "<наименование поставки>,<стоимость товара>,<номер заказа (при наличии)>\n" +
                    "<артикул товара>,<количество>,<цена>\n" +
                    "<артикул товара>,<количество>,<цена>\n"

        } catch (e: Exception) {
            log.error("Failed to process cmd", e)
            msg.text = "Не удалось обработать команду"
        }
        return msg
    }
}
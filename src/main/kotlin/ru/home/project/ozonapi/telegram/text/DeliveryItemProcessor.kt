package ru.home.project.ozonapi.telegram.text

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.impl.ChinaOrdersService

/**
 * @author rlagay
 */
@Component
class DeliveryItemProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val ordersRepository: ChinaOrdersRepository
): TextInputProcessor {
    companion object {
        val log: Logger = LoggerFactory.getLogger(DeliveryItemProcessor::class.java)
    }

    override fun processInput(input: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val chatId = update.message.chatId

        val isCommand = update.message.isCommand
        if (isCommand) {
            return null
        }
        msg.chatId = update.message?.chatId.toString()

        try {
            val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.AddDelivery)
            if (chat == null || chat.action != ActionType.AddDelivery || input.contains(",")) {
                return null
            }
            val order = ordersRepository.getChinaOrderEntityByDelivered(false)
                .find { it.number == input.trim() || it.supplier + " " + it.stockCost.toString().substringBefore(".") == input.trim() }
            if (order?.id == null) {
                msg.text = "Не удалось найти поставку"
            } else {
                chat.deliveryId = order.id
                telegramChatRepository.save(chat)
                msg.text = "Добавьте данные по доставке в формате: \n" +
                        "<стоимость доставки>,<масса груза>,<объем груза (при наличии)>\n"
            }
            return msg
        } catch (e: Exception) {
            log.error("Failed to add order", e)
            msg.text = "Не удалось обработать сообщение"

            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddOrder)

            return msg
        }
    }
}
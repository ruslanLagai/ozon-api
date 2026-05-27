package ru.home.project.ozonapi.telegram.text

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.repository.ChinaOrdersRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository

/**
 * @author rlagay
 */
@Component
class FulfilmentItemProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val ordersRepository: ChinaOrdersRepository
): TextInputProcessor {
    companion object {
        val log: Logger = LoggerFactory.getLogger(FulfilmentItemProcessor::class.java)
    }

    @Transactional
    override fun processInput(input: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val chatId = update.message.chatId

        val isCommand = update.message.isCommand
        if (isCommand) {
            return null
        }
        msg.chatId = update.message?.chatId.toString()

        try {
            val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.AddFulfilment)
            if (chat == null || input.contains(",")) {
                return null
            }
            val order = ordersRepository.getChinaOrderEntityByDelivered(true)
                .find {
                    val builder = StringBuilder().append(it.supplier)
                    if (it.number != null) {
                        builder.append(" №${it.number}")
                    }
                    builder.append(" от " + it.orderDate + " на сумму " + it.stockCost)
                    builder.toString() == input.trim()
                }
            if (order?.id == null) {
                msg.text = "Не удалось найти поставку"
            } else {
                chat.deliveryId = order.id!!
                telegramChatRepository.save(chat)
                msg.text = "Введите стоимость ФФ"
            }
            return msg
        } catch (e: Exception) {
            log.error("Failed to add order", e)
            msg.text = "Не удалось обработать сообщение"

            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddFulfilment)

            return msg
        }
    }
}
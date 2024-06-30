package ru.home.project.ozonapi.telegram.text

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.OrdersService

/**
 * @author rlagay
 */
@Component
class AddDeliveryInputProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val ordersService: OrdersService
): TextInputProcessor {

    private val errorMsg = "Добавьте данные по поставке в формате: \n" +
            "<стоимость доставки>,<масса груза>,<вес груза>"
    companion object {
        val log: Logger = LoggerFactory.getLogger(AddDeliveryInputProcessor::class.java)
    }

    override fun processInput(input: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val chatId = update.message.chatId

        val isCommand = update.message.isCommand
        if (isCommand) {
            return null
        }
        msg.chatId = update.message?.chatId.toString()


        val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.AddDelivery)

        if (chat == null || chat.action != ActionType.AddDelivery || !input.contains(",")) {
            return null
        }
        try {
            val data = input.split(delimiters = arrayOf(",", ";"), ignoreCase = false)

            if (data.size > 3 || data.size < 2) {
                msg.text = errorMsg
                return msg
            }
            val costs = data[0].toDouble()
            val mass = data[1].toDouble()
            val volume = if (data.size == 3) data[2].toDouble() else 0.0

            ordersService.addDelivery(orderId = chat.deliveryId, deliveryCost = costs, mass = mass, volume = volume)

            msg.text = "Данные по доставке успешно добавлены"
            return msg
        } catch (e: NumberFormatException) {
            log.error("Failed to parse data", e)
            msg.text = "Некорректный формат данных, не удалось обработать 'массу' / 'сумму'"
            return msg
        } catch (e: Exception) {
            log.error("Failed to add order", e)
            msg.text = "Не удалось сохранить данные по доставке"
            return msg
        } finally {
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddDelivery)
        }
    }
}
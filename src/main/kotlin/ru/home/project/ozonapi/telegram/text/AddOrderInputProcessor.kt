package ru.home.project.ozonapi.telegram.text

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.dto.request.ProductRequest
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.OrdersService

/**
 * @author rlagay
 */
@Component
class AddOrderInputProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val ordersService: OrdersService
): TextInputProcessor {

    private val errorMsg = "Добавьте данные по поставке в формате: \n" +
            "<наименование поставки>,<стоимость товара>,<номер заказа (при наличии)>\n" +
            "<артикул товара>,<количество>,<цена>\n" +
            "<артикул товара>,<количество>,<цена>\n"
    companion object {
        val log: Logger = LoggerFactory.getLogger(AddOrderInputProcessor::class.java)
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
            val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.AddOrder)

            if (chat == null || chat.action != ActionType.AddOrder) {
                return null
            }

            val data = input.split(delimiters = arrayOf("\n"), ignoreCase = false)
            val order = data[0].split(delimiters = arrayOf(",", ";"), ignoreCase = false)
            val productItems = data.filter { it != data[0] }
                .map { it.split(delimiters = arrayOf(",", ";"), ignoreCase = false) }
                .toList()

            if (data.size < 2) {
                msg.text = errorMsg
                return msg
            }
            for (list in productItems) {
                if (list.size != 3) {
                    msg.text = errorMsg
                    return msg
                }
            }

            val products = productItems.map { ProductRequest(artikul = it[0], quantity = it[1].toInt(), price = it[2].toDouble()) }.toList()
            ordersService.saveNewOrder(order[0], order[1].toDouble(), order[2], products)

            msg.text = "Поставка успешно добавлена"
            return msg
        } catch (e: NumberFormatException) {
            log.error("Failed to parse data", e)
            msg.text = "Некорректный формат данных, не удалось обработать 'количество' / 'цену'"
            return msg
        } catch (e: Exception) {
            log.error("Failed to add order", e)
            msg.text = "Не удалось сохранить данные по поставке"
            return msg
        } finally {
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddOrder)
        }
    }
}
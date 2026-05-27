package ru.home.project.ozonapi.telegram.text

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.exception.InvalidChineOrderException
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository
import ru.home.project.ozonapi.service.AdditionalServicesForCostPriceService
import ru.home.project.ozonapi.service.CrossDocService

/**
 * @author rlagay
 */
@Component
class FulfilmentDataInputProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val positionRepository: PositionRepository,
    val fulfilmentAdditionalService: AdditionalServicesForCostPriceService,
    val crossDocService: CrossDocService
): TextInputProcessor {

    private val log = LoggerFactory.getLogger(FulfilmentDataInputProcessor::class.java)

    override fun processInput(input: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val chatId = update.message.chatId
        val data = input.split(delimiters = arrayOf(",", ";"), ignoreCase = false)
        val isCommand = update.message.isCommand
        if (isCommand) {
            return null
        }

        try {
            val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.AddFulfilment)

            if (chat == null || chat.action != ActionType.AddFulfilment || data[0].length > 8) {
                return null
            }

            if (data.size != 1) {
                msg.chatId = update.message?.chatId.toString()
                msg.text = "Некорректный формат ввода данные"
                return msg
            }

            fulfilmentAdditionalService.updateCostPrice(chat.deliveryId, data[0].toDouble())
            crossDocService.linkWithOrders(chat.deliveryId)

            msg.chatId = update.message?.chatId.toString()
            msg.text = "Услуги ФФ добавлены в себестоимость FIFO"
            return msg
        } catch (_: NumberFormatException) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Некорректный формат данных, не удалось обработать 'стоимость ФФ'"
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddFulfilment)
            return msg
        } catch (e: InvalidChineOrderException) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Поставка не найдена"
            log.error("Поставка не найдена", e)
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddFulfilment)
            return msg
        } catch (e: Exception) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Не обновить стоимость ФФ"
            log.error("Error received", e)
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddFulfilment)
            return msg
        }
    }
}
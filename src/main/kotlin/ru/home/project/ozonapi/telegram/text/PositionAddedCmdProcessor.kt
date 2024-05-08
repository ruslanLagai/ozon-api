package ru.home.project.ozonapi.telegram.text

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.entity.PositionEntity
import ru.home.project.ozonapi.event.PositionAddedEvent
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository

/**
 * @author rlagay
 */
@Component
class PositionAddedCmdProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val positionRepository: PositionRepository,
    val publisher: ApplicationEventPublisher
): TextInputProcessor {

    override fun processInput(input: String, update: Update): SendMessage? {
        val msg = SendMessage()
        val chatId = update.message.chatId
        val data = input.split(delimiters = arrayOf(",", ";"), ignoreCase = false)
        val isCommand = update.message.isCommand
        if (isCommand) {
            return null
        }

        try {
            val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.AddPosition)

            if (chat == null || chat.action != ActionType.AddPosition) {
                return null
            }

            if (data.size != 5) {
                msg.chatId = update.message?.chatId.toString()
                msg.text = "Некорректный формат ввода данные, используйте: <название>,<себестоимость>,<доп расходы>,<артикул>,<ozonId>"
                return msg
            }

            val position = PositionEntity(name =  data[0].trim(), costPrice = data[1].toDouble(), additionalCost = data[2].toDouble(),
                artikul = data[3].trim(), ozonId = data[4].trim())
            positionRepository.save(position)

            msg.chatId = update.message?.chatId.toString()
            msg.text = "Товар успешно добавлен"
            publisher.publishEvent(PositionAddedEvent(data[0].trim()))
            return msg
        } catch (e: NumberFormatException) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Некорректный формат данных, не удалось обработать 'себестоимость' и/или 'доп расходы'"
            return msg
        } catch (e: Exception) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Не удалось сохранить товар"
            return msg
        } finally {
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.AddPosition)
        }
    }
}
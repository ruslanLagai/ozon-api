package ru.home.project.ozonapi.telegram.text

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.entity.ActionType
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.repository.TelegramChatRepository

/**
 * @author rlagay
 */
@Component
class PositionEditedCmdProcessor(
    val telegramChatRepository: TelegramChatRepository,
    val positionRepository: PositionRepository
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
            val chat = telegramChatRepository.getByChatIdAndStateAndAction(chatId, true, ActionType.EditPosistion)

            if (chat == null || chat.action != ActionType.EditPosistion) {
                return null
            }

            if (data.size != 3) {
                msg.chatId = update.message?.chatId.toString()
                msg.text = "Некорректный формат ввода данных, используйте: <артикул>,<себестоимость>,<доп расходы>"
                return msg
            }

            val artikul = data[0]
            if (StringUtils.isBlank(artikul)) {
                msg.chatId = update.message?.chatId.toString()
                msg.text = "Некорректный формат ввода данных: отсутствует артикул, используйте: <артикул>,<себестоимость>,<доп расходы>"
                return msg
            }

            val costPrice = data[1]
            val addCosts = data[2]
            if (StringUtils.isBlank(costPrice)) {
                positionRepository.updateAddCostsByArtikul(artikul, addCosts = addCosts.toDouble())
            } else if (StringUtils.isBlank(addCosts)) {
                positionRepository.updateCostPriceByArtikul(artikul, costPrice = costPrice.toDouble())
            } else {
                positionRepository.updateByArtikul(artikul = artikul, costPrice = costPrice.toDouble(), addCosts = addCosts.toDouble())
            }

            msg.chatId = update.message?.chatId.toString()
            msg.text = "Данные по себестоимости успешно изменены"
            return msg
        } catch (e: NumberFormatException) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Некорректный формат данных, не удалось обработать 'себестоимость' и/или 'доп расходы'"
            return msg
        } catch (e: Exception) {
            msg.chatId = update.message?.chatId.toString()
            msg.text = "Не удалось обновить данные по товару"
            return msg
        } finally {
            telegramChatRepository.updateStateByChatIdAndAction(chatId, false, ActionType.EditPosistion)
        }
    }
}
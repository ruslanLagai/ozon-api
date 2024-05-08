package ru.home.project.ozonapi.telegram.commands

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
class EditPositionCmdProcessor(
    val telegramChatRepository: TelegramChatRepository
): CmdProcessor {

    override fun processCmd(command: String, update: Update): SendMessage? {

        telegramChatRepository.save(TelegramChatEntity(chatId = update.message.chatId, positionName = "",
            action = ActionType.EditPosistion))

        val msg = SendMessage()
        msg.chatId = update.message?.chatId.toString()
        msg.text = "Чтобы изменить данные по себестоимости товара, введите данные в формате <артикул>,<себестоимость>,<доп расходы>\n" +
                "Если требуется изменить одно поле, оставьте поле пустым"
        return msg
    }
}
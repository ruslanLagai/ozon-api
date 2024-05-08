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
class AddPositionCmdProcessor(
    val telegramChatRepository: TelegramChatRepository
): CmdProcessor {

    override fun processCmd(command: String, update: Update): SendMessage? {

        telegramChatRepository.save(TelegramChatEntity(chatId = update.message.chatId, positionName = "",
            action = ActionType.AddPosition, state = true))

        val msg = SendMessage()
        msg.chatId = update.message?.chatId.toString()
        msg.text = "Добавьте товар в формате <название>,<себестоимость>,<доп расходы>,<артикул>,<ozonId>"
        return msg
    }
}
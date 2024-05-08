package ru.home.project.ozonapi.telegram.text

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.home.project.ozonapi.telegram.commands.CmdProcessor
import ru.home.project.ozonapi.telegram.commands.RefundsCmdProcessor

/**
 * @author rlagay
 */
@Component
class CommandProcessor(
    val calculationsCmdProcessor: CmdProcessor,
    val addPositionCmdProcessor: CmdProcessor,
    val positionsCmdProcessor: CmdProcessor,
    val editPositionCmdProcessor: CmdProcessor,
    val refundsCmdProcessor: CmdProcessor
): TextInputProcessor {

    private val commandProcessors: MutableMap<String, (String, Update) -> SendMessage?> = mutableMapOf(
        Pair("/calculations") { command: String, update: Update -> calculationsCmdProcessor.processCmd(command, update) },
        Pair("/positions") { command: String, update: Update -> positionsCmdProcessor.processCmd(command, update) },
        Pair("/add_position") { command: String, update: Update -> addPositionCmdProcessor.processCmd(command, update) },
        Pair("/edit_position") { command: String, update: Update -> editPositionCmdProcessor.processCmd(command, update) },
        Pair("/refunds") { command: String, update: Update -> refundsCmdProcessor.processCmd(command, update) }
    )

    override fun processInput(input: String, update: Update): SendMessage? {
        val isCommand = update.message.isCommand
        if (isCommand) {
            return commandProcessors.getOrDefault(input) { _, _ -> null }.invoke(input, update)
        }
        return null
    }
}
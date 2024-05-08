package ru.home.project.ozonapi.telegram.commands

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * @author rlagay
 */
interface CmdProcessor {

    fun processCmd(command: String, update: Update) : SendMessage?
}
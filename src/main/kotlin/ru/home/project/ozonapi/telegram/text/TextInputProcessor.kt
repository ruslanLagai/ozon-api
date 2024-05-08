package ru.home.project.ozonapi.telegram.text

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * @author rlagay
 */
interface TextInputProcessor {

    fun processInput(input: String, update: Update): SendMessage?
}
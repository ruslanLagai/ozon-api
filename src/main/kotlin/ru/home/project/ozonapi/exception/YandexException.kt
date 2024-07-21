package ru.home.project.ozonapi.exception

/**
 * @author rlagay
 */
class YandexException(msg: String = "", code: Int? = null)
    : RuntimeException("Failed to get info from Yandex, code '$code', message: '$msg'")
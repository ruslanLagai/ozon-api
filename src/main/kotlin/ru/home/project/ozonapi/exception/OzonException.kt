package ru.home.project.ozonapi.exception

/**
 * @author rlagay
 */
class OzonException(msg: String = "", code: Int?)
    : RuntimeException("Failed to get info from Ozon, code '$code', message: '$msg'")
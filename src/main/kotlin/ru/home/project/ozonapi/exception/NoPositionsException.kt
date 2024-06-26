package ru.home.project.ozonapi.exception

/**
 * @author rlagay
 */
class NoPositionsException(msg: String = "No positions found in DB") : RuntimeException(msg)
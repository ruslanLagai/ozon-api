package ru.home.project.ozonapi.model

import ru.home.project.ozonapi.exception.InvalidClusterException

/**
 * @author rlagay
 */
enum class Cluster(val value: String) {
    MOSCOW_EAST("Москва-Восток и Дальние регионы"),
    MOSCOW_WEST("Москва-Запад"),
    CENTER("Центр"),
    SPB("Санкт-Петербург и СЗО"),
    VOLGA("Поволжье"),
    DON("Дон"),
    SOUTH("Юг"),
    URAL("Урал"),
    SIBERIA("Сибирь"),
    FAR_EAST("Дальний Восток"),
    KALININGRAD("Калининград"),
    KZ("Казахстан"),
    BU("Республика Беларусь");
}

fun parse(value: String): Cluster {
    val cluster = Cluster.entries.find { it.value.contentEquals(value, true)}
    if (cluster == null) {
        throw InvalidClusterException("Invalid cluster name '{$value}'")
    }
    return cluster
}

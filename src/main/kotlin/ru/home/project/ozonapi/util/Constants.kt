package ru.home.project.ozonapi.util

import org.openapitools.client.models.OrderStatusType
import ru.home.project.ozonapi.model.Cluster

/**
 * @author rlagay
 */

const val lastDayDate = "Посчитать за сегодня"
const val lastTwoDaysDate = "Посчитать за последние 2 дня"
const val forCurrentWeek = "Посчитать с начала недели"
const val forCurrentMonth = "Посчитать с начала месяца"
const val addDelivery = "Добавить поставку"
const val changeDelivery = "Изменить поставку"

const val allItems = "По всем товарам"
const val refundsByCluster = "Возвраты по кластерам"
const val refundsStatistics = "Возвраты по позициям"

const val yandex = "Яндекс маркет"
const val ozon = "Ozon"

val moscowWestStocks = setOf("Гривно", "Давыдовское", "Павловская Слобода", "Петровское", "Хоругвино")
val moscowEastStocks = setOf("Жуковский", "Ногинск", "Пушкино")
val farEastStocks = setOf("Хабаровск")
val centerStocks = setOf("Софьино", "Тверь")
val spbStocks = setOf("Санкт-Петербург", "Бугры", "Шушары")
val volgaStocks = setOf("Казань", "Нижний Новгород", "Самара")
val donStocks = setOf("Воронеж", "Волгоград", "Ростов-на-Дону")
val southStocks = setOf("Адыгейск", "Новороссийск")
val uralStocks = setOf("Екатеринбург")
val siberiaStocks = setOf("Красноярск", "Новосибирск")
val kaliningradStocks = setOf("Калининград")
val kzStocks = setOf("Астана", "Алматы")
val belarusStocks = setOf("Минск")

val clustersMap = mapOf(
    Pair(Cluster.MOSCOW_WEST, moscowWestStocks),
    Pair(Cluster.MOSCOW_EAST, moscowEastStocks),
    Pair(Cluster.CENTER, centerStocks),
    Pair(Cluster.SPB, spbStocks),
    Pair(Cluster.VOLGA, volgaStocks),
    Pair(Cluster.DON, donStocks),
    Pair(Cluster.SOUTH, southStocks),
    Pair(Cluster.URAL, uralStocks),
    Pair(Cluster.SIBERIA, siberiaStocks),
    Pair(Cluster.KALININGRAD, kaliningradStocks),
    Pair(Cluster.KZ, kzStocks),
    Pair(Cluster.FAR_EAST, farEastStocks),
    Pair(Cluster.BU, belarusStocks)
)

val yandexFinalStatuses = setOf(
    OrderStatusType.DELIVERED,
    OrderStatusType.CANCELLED,
    OrderStatusType.RETURNED
)

val yandexInDeliveryStatuses = setOf(
    OrderStatusType.DELIVERY,
    OrderStatusType.PICKUP
)
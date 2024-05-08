package ru.home.project.ozonapi.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * @author rlagay
 */

private val dateFormatters: List<DateTimeFormatter> = listOf(
    DateTimeFormatter.ofPattern("dd.MM.yyyy"),
    DateTimeFormatter.ofPattern("d.MM.yyyy"),
    DateTimeFormatter.ofPattern("d.MM.yy"),
    DateTimeFormatter.ofPattern("dd.MM.yy")
)

private val datesFromMap: Map<String, OffsetDateTime> = mapOf(
    Pair(lastDayDate, LocalDate.now().atStartOfDay().atOffset(ZoneOffset.ofHours(0))),
    Pair(lastTwoDaysDate, LocalDate.now().minusDays(1).atStartOfDay().atOffset(ZoneOffset.ofHours(0))),
    Pair(forCurrentWeek, LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong() - 1).atStartOfDay().atOffset(ZoneOffset.ofHours(0))),
    Pair(forCurrentMonth, LocalDate.now().minusDays(LocalDate.now().dayOfMonth.toLong() - 1).atStartOfDay().atOffset(ZoneOffset.ofHours(0)))
)

private val datesToMap: Map<String, OffsetDateTime> = mapOf(
    Pair(lastDayDate, LocalDateTime.now().atOffset(ZoneOffset.ofHours(0))),
    Pair(lastTwoDaysDate, LocalDateTime.now().atOffset(ZoneOffset.ofHours(0))),
    Pair(forCurrentWeek, LocalDateTime.now().atOffset(ZoneOffset.ofHours(0))),
    Pair(forCurrentMonth, LocalDateTime.now().atOffset(ZoneOffset.ofHours(0)))
)

private val parseFromDate = { date: String, formatter: DateTimeFormatter ->
    val localDate = LocalDate.parse(date, formatter).atTime(LocalTime.MIN)
    OffsetDateTime.of(localDate, ZoneOffset.UTC)
}

private val parseToDate = { date: String, formatter: DateTimeFormatter ->
    val localDate = LocalDate.parse(date, formatter).atTime(LocalTime.MAX)
    OffsetDateTime.of(localDate, ZoneOffset.UTC)
}

fun parseFromDate(date: String) : OffsetDateTime {
    for (formatter: DateTimeFormatter in dateFormatters) {
        try {
            return datesFromMap[date] ?: parseFromDate.invoke(date, formatter)
        } catch (_: DateTimeParseException) {}
    }
    throw RuntimeException("Failed to parse from date $date")
}

fun parseToDate(date: String) : OffsetDateTime {
    for (formatter: DateTimeFormatter in dateFormatters) {
        try {
            return datesToMap[date] ?: parseToDate.invoke(date, formatter)
        } catch (_: DateTimeParseException) {
        }
    }
    throw RuntimeException("Failed to parse to date $date")
}
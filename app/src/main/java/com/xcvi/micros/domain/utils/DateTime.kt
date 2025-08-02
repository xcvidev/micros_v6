package com.xcvi.micros.domain.utils

import android.os.Build
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale


/**
 * Stats
 */
data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    init {
        require(month in 1..12) { "Month must be between 1 and 12" }
    }

    override fun compareTo(other: YearMonth): Int =
        compareValuesBy(this, other, { it.year }, { it.month })

    fun next(): YearMonth =
        if (month == 12) YearMonth(year + 1, 1) else YearMonth(year, month + 1)
}

fun List<Int>.groupByWeek(): Map<Int, List<Int>> {
    if (this.isEmpty()) return emptyMap()
    val startMonday = this.minOfOrNull { it.getStartOfWeek() } ?: return emptyMap()
    val endMonday = this.maxOfOrNull { it.getStartOfWeek() } ?: return emptyMap()

    val allMondays = generateSequence(startMonday) { previous ->
        val next = previous + 7
        if (next > endMonday) null else next
    }.toList()

    val weeksMap = allMondays.associateWith { mutableListOf<Int>() }

    for (epochDay in this) {
        val offsetDays = epochDay - startMonday
        if (offsetDays < 0 || epochDay > endMonday + 6) continue // skip out-of-range days

        val weekStart = startMonday + (offsetDays / 7) * 7
        weeksMap[weekStart]?.add(epochDay)
    }
    return weeksMap
}


fun YearMonth.getStartOfMonth(): Int {
    return LocalDate(this.year, this.month, 1).toEpochDays()
}

fun generateMondaysBetween(startMonday: Int, endMonday: Int): List<Int> {
    val mondays = mutableListOf<Int>()
    var current = startMonday
    while (current <= endMonday) {
        mondays.add(current)
        current += 7 // add 7 days in epoch days
    }
    return mondays
}

fun generateMonthsBetween(startDate: LocalDate, endDate: LocalDate): List<YearMonth> {
    val startYM = YearMonth(startDate.year, startDate.monthNumber)
    val endYM = YearMonth(endDate.year, endDate.monthNumber)

    val result = mutableListOf<YearMonth>()

    var current = if (startYM <= endYM) startYM else endYM
    val final = if (startYM <= endYM) endYM else startYM

    while (current <= final) {
        result.add(current)
        current = current.next()
    }

    // If the original order was descending, reverse the result
    return if (startYM <= endYM) result else result.reversed()
}

/**
 * Get Current
 */
fun getNow(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun getToday(): Int {
    val instant = Instant.fromEpochMilliseconds(getNow())
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date.toEpochDays()
}

/**
 * Int -> Long
 */
fun Int.getStartTimestamp(): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun Int.getEndTimestamp(): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(23, 59, 59, 999).toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

fun Int.getTimestamp(hour: Int, minute: Int, seconds: Int): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(hour, minute, seconds).toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

fun Int.getTimestamp(hour: Int, minute: Int): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(hour, minute, 0, 0).toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}


/**
 * Long -> Int
 */

fun Long.getEpochDate(): Int {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date.toEpochDays()
}


/**
 * Int -> LocalDate
 */

fun Int.getLocalDate(): LocalDate {
    return try {
        LocalDate.fromEpochDays(this)
    } catch (e: Exception) {
        LocalDate.fromEpochDays(0)
    }
}

fun Int.getStartOfWeek(): Int {
    try {
        val localDate = LocalDate.fromEpochDays(this)
        val dayOfWeek = localDate.dayOfWeek.isoDayNumber  // Monday=1, Sunday=7

        val daysToSubtract = (dayOfWeek - 1) // how many days since Monday
        val monday = localDate.minus(DatePeriod(days = daysToSubtract))
        return monday.toEpochDays()
    } catch (e: Exception) {
        return this
    }
}

fun Int.getEndOfWeek(): Int {
    return this.getStartOfWeek() + 6
}

fun Int.getStartOfMonth(): Int {
    val localDate = LocalDate.fromEpochDays(this)
    return LocalDate(localDate.year, localDate.month, 1).toEpochDays()
}

fun Int.getEndOfMonth(): Int {
    val localDate = LocalDate.fromEpochDays(this)
    val nextMonth = LocalDate(localDate.year, localDate.monthNumber + 1, 1)
    return nextMonth.toEpochDays() - 1
}

fun LocalDate.startOfNextMonth(): LocalDate {
    return if (this.monthNumber == 12) {
        LocalDate(this.year + 1, 1, 1)
    } else {
        LocalDate(this.year, this.monthNumber + 1, 1)
    }
}

fun getDaysInMonth(month: Int, year: Int): Int {
    require(month in 1..12) { "Month must be between 1 and 12" }

    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> throw IllegalArgumentException("Invalid month: $month")
    }
}

fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}


/**
 * Long -> DateTime
 */

fun Long.getLocalDate(): LocalDate {
    try{
        val instant = Instant.fromEpochMilliseconds(this)
        val timeZone = TimeZone.currentSystemDefault()
        val localDate = instant.toLocalDateTime(timeZone)
        return localDate.date
    } catch (e: Exception){
        return LocalDate.fromEpochDays(0)
    }
}

fun Long.getLocalDateTime(): LocalDateTime {
    try{
        val instant = Instant.fromEpochMilliseconds(this)
        val timeZone = TimeZone.currentSystemDefault()
        return instant.toLocalDateTime(timeZone)
    } catch (e: Exception){
        return getLocalDateTime()
    }
}

fun LocalDateTime.getTime(): String{
    val minute = if(minute < 10) "0$minute" else minute
    return "${this.time.hour}:${minute}"
}


/**
 * Int -> Formatted Date
 */
fun LocalDate.monthFormatted(short: Boolean = false, locale: Locale = Locale.getDefault()): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.month.getDisplayName(style, locale).lowercase()
            .replaceFirstChar { it.uppercase() }

    } else {
        // Fallback: format manually (non-localized)
        val name = this.month.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun LocalDate.dayOfWeekFormatted(
    short: Boolean = false,
    locale: Locale = Locale.getDefault()
): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.dayOfWeek.getDisplayName(style, locale).lowercase().replaceFirstChar { it.uppercase() }
    } else {
        // Fallback: format manually (non-localized)
        val name = this.dayOfWeek.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun Int.formatEpochDate(
    short: Boolean = true, showDayOfWeek: Boolean = false
): String {
    val date = LocalDate.fromEpochDays(this)
    val month = date.monthFormatted(short)
    val dayOfWeek = date.dayOfWeekFormatted(short)

    return if (showDayOfWeek) {
        "$dayOfWeek, $month ${date.dayOfMonth}"
    } else {
        "$month ${date.dayOfMonth}"
    }
}


/**
 * Long -> Formatted Date
 */

fun Long.formatTimestamp(
    short: Boolean = true,
    showDayOfWeek: Boolean = false,
    showTime: Boolean = false
): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone)

    val dateFormatted =
        localDate.date.toEpochDays().formatEpochDate(short = short, showDayOfWeek = showDayOfWeek)
    val timeFormatted = if (showTime) {
        "${localDate.time.hour}:${localDate.time.minute}"
    } else {
        ""
    }

    return "$dateFormatted $timeFormatted"
}













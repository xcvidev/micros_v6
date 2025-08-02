package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.utils.DailySummary
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.respostory.WeightRepository
import com.xcvi.micros.domain.utils.YearMonth
import com.xcvi.micros.domain.utils.generateMonthsBetween
import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.getStartOfMonth
import com.xcvi.micros.domain.utils.getStartOfWeek


class StatsUseCases(
    private val portionRepository: PortionRepository,
    private val weightRepository: WeightRepository,
)


private fun <T : DailySummary> List<T>.groupByMonth(): Map<Int, List<T>> {
    val end = this.firstOrNull()?.date ?: return emptyMap()
    val start = this.lastOrNull()?.date ?: return emptyMap()

    val byMonthRaw = this.groupBy {
        val date = it.date.getLocalDate()
        val ym = YearMonth(date.year, date.monthNumber)
        ym
    }

    val allMonths = generateMonthsBetween(
        start.getLocalDate(),
        end.getLocalDate()
    ).associateWith { mutableListOf<T>() }

    for (entry in byMonthRaw) {
        val month = entry.key
        val list = entry.value
        allMonths[month]?.addAll(list)
    }
    return allMonths.mapKeys {
        it.key.getStartOfMonth()
    }
}

private fun <T : DailySummary> List<T>.groupByWeek(): Map<Int, List<T>> {
    if (this.isEmpty()) return emptyMap()
    val dates = this.map { it.date }
    val startMonday = dates.minOf { it.getStartOfWeek() }
    val endMonday = dates.maxOf { it.getStartOfWeek() }

    val allMondays = generateSequence(startMonday) { previous ->
        val next = previous + 7
        if (next > endMonday) null else next
    }.toList()

    val weeksMap = allMondays.associateWith { mutableListOf<T>() }

    for (summary in this) {
        val epochDay = summary.date
        val offsetDays = epochDay - startMonday
        if (offsetDays < 0 || epochDay > endMonday + 6) continue // skip out-of-range days

        val weekStart = startMonday + (offsetDays / 7) * 7
        weeksMap[weekStart]?.add(summary)
    }
    return weeksMap
}

private fun <T : DailySummary> List<T>.groupByDay(): Map<Int, List<T>> {
    if (this.isEmpty()) return emptyMap()

    val end = this.firstOrNull()?.date ?: return emptyMap()
    val start = this.lastOrNull()?.date ?: return emptyMap()

    val allDays = generateSequence(start) { previous ->
        val next = previous + 1
        if (next > end) null else next
    }.toList().associateWith { mutableListOf<T>() }

    val byDayRaw = this.groupBy { it.date }

    for (entry in byDayRaw) {
        val date = entry.key
        val list = entry.value
        allDays[date]?.addAll(list)
    }
    return allDays.mapKeys {
        it.key
    }
}


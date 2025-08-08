package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.utils.DailySummary
import com.xcvi.micros.domain.model.utils.FilterType
import com.xcvi.micros.domain.model.weight.WeightSummary
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.respostory.WeightRepository
import com.xcvi.micros.domain.utils.YearMonth
import com.xcvi.micros.domain.utils.generateMonthsBetween
import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.getStartOfMonth
import com.xcvi.micros.domain.utils.getStartOfWeek
import com.xcvi.micros.domain.utils.getToday
import kotlinx.datetime.LocalDate


import com.xcvi.micros.domain.utils.getLocalDate
import com.xcvi.micros.domain.utils.monthFormatted


class StatsUseCases(
    private val portionRepository: PortionRepository,
    private val weightRepository: WeightRepository,
) {

    suspend fun getData(): Triple<List<Int>, List<MacrosSummary>, List<WeightSummary>> {
        val weight = weightRepository.getHistory()
        val foods = portionRepository.getAllSummaries()
        val years1 = weight.map { it.date.getLocalDate().year }
        val years2 = foods.map { it.date.getLocalDate().year }
        val years = (years1 + years2).distinct()
        return Triple(years, foods, weight)
    }


    fun filterWeights(
        year: Int?,
        filter: FilterType,
        weights: List<WeightSummary>,
        unit: WeightUnit,
    ): List<WeightSummary> {
        val start = if (year == null) 0 else LocalDate(year, 1, 1).toEpochDays()
        val end = if (year == null) getToday() else LocalDate(year, 12, 31).toEpochDays()
        return when (filter) {

            FilterType.MONTH -> weights
                .filter { it.date in (start..end) }
                .groupByMonth()
                .map { (startOfMonth, summaries) ->
                    summaries.average(startOfMonth, byMonth = true, unit = unit)
                }


            FilterType.WEEK -> weights
                .filter { it.date in (start..end) }
                .groupByWeek()
                .map { (startOfWeek, summaries) ->
                    summaries.average(startOfWeek, byMonth = false, unit = unit)
                }


            FilterType.DAY -> weights
                .filter { it.date in (start..end) }
                .groupByDay()
                .map { (date, summaries) ->
                    summaries.average(date, byMonth = false, unit = unit)
                }

        }
    }

    fun filterFoods(
        year: Int?,
        filter: FilterType,
        summariesOfDate: List<MacrosSummary>,
    ): List<MacrosSummary> {
        val start = if (year == null) 0 else LocalDate(year, 1, 1).toEpochDays()
        val end = if (year == null) getToday() else LocalDate(year, 12, 31).toEpochDays()
        return when (filter) {

            FilterType.WEEK -> summariesOfDate
                .filter { it.date in (start..end) }
                .groupByWeek()
                .map { (startOfWeek, summaries) ->
                    summaries.average(
                        date = startOfWeek
                    )
                }


            FilterType.MONTH -> summariesOfDate
                .filter { it.date in (start..end) }
                .groupByMonth()
                .map { (startOfMonth, summaries) ->
                    summaries.average(
                        date = startOfMonth
                    )
                }

            FilterType.DAY -> summariesOfDate.filter { it.date in (start..end) }.sortedBy { it.date }
        }
    }


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
}


fun List<MacrosSummary>.average(date: Int): MacrosSummary {
    if (this.isEmpty()) {
        return MacrosSummary.empty().copy(date = date)
    }

    val summaries = this
    val latestGoal = summaries.maxByOrNull { it.date }?.goal ?: Macros()
    val calories = summaries.sumOf { it.actual.calories } / summaries.size
    val protein = summaries.sumOf { it.actual.protein } / summaries.size
    val carbohydrates = summaries.sumOf { it.actual.carbohydrates } / summaries.size
    val fats = summaries.sumOf { it.actual.fats } / summaries.size

    return MacrosSummary(
        date = date,
        actual = Macros(calories, protein, carbohydrates, fats),
        goal = latestGoal
    )
}


fun List<WeightSummary>.average(date: Int, byMonth: Boolean, unit: WeightUnit): WeightSummary {
    val labelWeek =
        date.getLocalDate().monthFormatted(true) + " " + date.getLocalDate().dayOfMonth
    val labelMonth = date.getLocalDate().monthFormatted(true).uppercase()
    val label = if (byMonth) labelMonth else labelWeek
    if (this.isEmpty()) {
        return WeightSummary(
            date = date,
            min = 0.0,
            max = 0.0,
            avg = 0.0,
            label = label,
            unit = unit
        )
    }

    val avg = this.sumOf { it.avg } / size
    return WeightSummary(
        date = date,
        min = this.minOfOrNull { it.min } ?: 0.0,
        max = this.maxOfOrNull { it.max } ?: 0.0,
        avg = avg,
        unit = this.firstOrNull()?.unit ?: WeightUnit.kg,
        label = label
    )
}

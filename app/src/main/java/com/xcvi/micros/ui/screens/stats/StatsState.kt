package com.xcvi.micros.ui.screens.stats

import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.utils.FilterType
import com.xcvi.micros.domain.model.weight.WeightSummary

data class StatsState(
    val filter: FilterType = FilterType.WEEK,
    val years: List<Int> = emptyList(),
    val weightsByDay: List<WeightSummary> = emptyList(),
    val weightsByWeek: List<WeightSummary> = emptyList(),
    val weightsByMonth: List<WeightSummary> = emptyList(),

    val caloriesByDay: List<Double?> = emptyList(),
    val calorieByWeek: List<Double?> = emptyList(),
    val caloriesByMonth: List<Double?> = emptyList(),
    val macrosByDay: List<MacrosSummary> = emptyList(),
    val macrosByWeek: List<MacrosSummary> = emptyList(),
    val macrosByMonth: List<MacrosSummary> = emptyList(),
)
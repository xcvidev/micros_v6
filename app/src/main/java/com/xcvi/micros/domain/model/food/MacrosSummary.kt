package com.xcvi.micros.domain.model.food

import com.xcvi.micros.domain.model.utils.DailySummary
import com.xcvi.micros.domain.utils.getToday

data class MacrosSummary(
    override val date: Int,
    val actual: Macros,
    val goal: Macros,
    val actualMinerals: Minerals = Minerals.empty(),
    val actualNutrients: Nutrients = Nutrients.empty(),
    val actualVitamins: Vitamins = Vitamins.empty(),
    val actualAminoacids: AminoAcids = AminoAcids.empty(),
): DailySummary {
    companion object {
        fun empty() = MacrosSummary(getToday(), Macros(), Macros())
    }

    fun isEmpty() = (actual.isEmpty() && goal.isEmpty())

    fun isNotEmpty() = !isEmpty()

    fun hasGoals() = goal.isNotEmpty()
}
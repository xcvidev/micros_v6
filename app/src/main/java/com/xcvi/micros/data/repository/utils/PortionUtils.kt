package com.xcvi.micros.data.repository.utils


import com.xcvi.micros.data.source.local.entity.food.*
import com.xcvi.micros.data.source.local.entity.food.relations.*
import com.xcvi.micros.data.source.local.entity.food.MacroGoalEntity
import com.xcvi.micros.data.source.local.entity.food.relations.MacrosWithDate
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Macros
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.utils.roundToInt
import kotlin.collections.map
import kotlin.collections.sortedBy

fun Portion.toEntity(): PortionEntity {
    return PortionEntity(
        barcode = food.barcode,
        date = date,
        meal = meal,
        amount = amount.toDouble(),
    )
}
fun PortionWithFood.toModel(): Portion {
    val defaultPortion = Portion(
        date = portion.date,
        amount = 100, // will be scaled
        meal = portion.meal,
        food = food.toModel()
    )
    return defaultPortion.scale(portion.amount.roundToInt())
}

fun MacrosWithDate?.getMacros(): Macros {
    if (this == null) return Macros()
    return Macros(
        calories = calories.roundToInt(),
        protein = protein,
        carbohydrates = carbohydrates,
        fats = fats
    )
}

fun mergeActualWithGoals(
    actuals: List<MacrosWithDate>,
    goals: List<MacroGoalEntity>
): List<MacrosSummary> {
    // Sort goals ascending by date for easier lookup (oldest first)
    val sortedGoals = goals.sortedBy { it.date }

    return actuals.map { actual ->
        // Find the closest goal with date <= actual.date
        val matchingGoal = sortedGoals.lastOrNull { it.date <= actual.date }

        val goalMacros = matchingGoal?.let {
            Macros(
                calories = it.calories.roundToInt(),
                protein = it.protein,
                carbohydrates = it.carbohydrates,
                fats = it.fats
            )
        } ?: Macros()  // fallback if no goal found

        val actualMacros = Macros(
            calories = actual.calories.roundToInt(),
            protein = actual.protein,
            carbohydrates = actual.carbohydrates,
            fats = actual.fats
        )

        MacrosSummary(
            date = actual.date,
            actual = actualMacros,
            goal = goalMacros
        )
    }
}

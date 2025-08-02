package com.xcvi.micros.data.repository.utils


import com.xcvi.micros.data.source.local.entity.food.*
import com.xcvi.micros.data.source.local.entity.food.relations.*
import com.xcvi.micros.data.source.local.entity.food.MacroGoalEntity
import com.xcvi.micros.data.source.local.entity.food.relations.MacrosWithDate
import com.xcvi.micros.domain.model.food.AminoAcids
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
        barcode = barcode,
        date = date,
        meal = meal,
        amount = amount.toDouble(),
    )
}
fun PortionWithFood.toModel(): Portion {
    val defaultPortion = Portion(
        barcode = food.barcode,
        name = food.name,
        amount = 100, // will be scaled
        date = portion.date,
        meal = portion.meal,
        isFavorite = food.isFavorite,
        nutrients = Nutrients(
            calories = food.calories.roundToInt(),
            protein = food.protein,
            carbohydrates = food.carbohydrates,
            fats = food.fats,
            saturatedFats = food.saturatedFats,
            fiber = food.fiber,
            sugars = food.sugars
        ),
        minerals = Minerals(
            calcium = food.calcium,
            iron = food.iron,
            magnesium = food.magnesium,
            potassium = food.potassium,
            sodium = food.sodium,
            zinc = food.zinc,
            fluoride = food.fluoride,
            iodine = food.iodine,
            phosphorus = food.phosphorus,
            manganese = food.manganese,
            selenium = food.selenium
        ),
        vitamins = Vitamins(
            vitaminA = food.vitaminA,
            vitaminB1 = food.vitaminB1,
            vitaminB2 = food.vitaminB2,
            vitaminB3 = food.vitaminB3,
            vitaminB4 = food.vitaminB4,
            vitaminB5 = food.vitaminB5,
            vitaminB6 = food.vitaminB6,
            vitaminB9 = food.vitaminB9,
            vitaminB12 = food.vitaminB12,
            vitaminC = food.vitaminC,
            vitaminD = food.vitaminD,
            vitaminE = food.vitaminE,
            vitaminK = food.vitaminK
        ),
        aminoAcids = AminoAcids(
            histidine = food.histidine,
            isoleucine = food.isoleucine,
            leucine = food.leucine,
            lysine = food.lysine,
            methionine = food.methionine,
            phenylalanine = food.phenylalanine,
            threonine = food.threonine,
            tryptophan = food.tryptophan,
            valine = food.valine
        )
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

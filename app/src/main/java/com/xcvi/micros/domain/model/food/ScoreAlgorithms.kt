package com.xcvi.micros.domain.model.food
import com.xcvi.micros.domain.utils.roundToInt
import kotlin.math.exp

fun scoreProtein(proteinPercent: Double): Double {
    return when {
        proteinPercent >= 90.0 -> 1.0
        else -> (proteinPercent / 90.0).coerceIn(0.0, 1.0)
    }
}

fun scoreCarbs(carbsPercent: Double): Double {
    return when {
        carbsPercent in 90.0..125.0 -> 1.0
        carbsPercent < 90.0 -> (carbsPercent / 90.0).coerceIn(0.0, 1.0)
        else -> {
            val overshoot = carbsPercent - 125.0
            val penalty = exp(overshoot / 25.0) - 1.0
            (1.0 - penalty).coerceIn(0.0, 1.0)
        }
    }
}

fun scoreFats(fatsPercent: Double): Double {
    return when {
        fatsPercent in 90.0..125.0 -> 1.0
        fatsPercent < 90.0 -> (fatsPercent / 90.0).coerceIn(0.0, 1.0)
        else -> {
            val overshoot = fatsPercent - 125.0
            val penalty = exp(overshoot / 25.0) - 1.0
            (1.0 - penalty).coerceIn(0.0, 1.0)
        }
    }
}


fun macroScoreAlgorithm(protein: Double, carbs: Double, fats: Double): Int {
    fun scoreProtein(value: Double): Double {
        return when {
            value >= 90.0 -> 1.0
            else -> (value / 90.0).coerceIn(0.0, 1.0)
        }
    }

    fun scoreCarbOrFat(value: Double): Double {
        return when {
            value in 90.0..125.0 -> 1.0
            value < 90.0 -> (value / 90.0).coerceIn(0.0, 1.0)
            else -> {
                val overshoot = value - 125.0
                val penalty = exp(overshoot / 25.0) - 1.0  // gentle at first, harsh after 150%
                (1.0 - penalty).coerceIn(0.0, 1.0)
            }
        }
    }

    val proteinScore = scoreProtein(protein)
    val carbScore = scoreCarbOrFat(carbs)
    val fatScore = scoreCarbOrFat(fats)

    val average = (proteinScore + carbScore + fatScore) / 3.0
    return (average * 100).roundToInt()
}
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
        carbsPercent in 95.0..102.5 -> 1.0
        carbsPercent < 95.0 -> (carbsPercent / 95.0).coerceIn(0.0, 1.0)
        else -> {
            val overshoot = carbsPercent - 102.5
            val penalty = exp(overshoot / 25.0) - 1.0
            (1.0 - penalty).coerceIn(0.0, 1.0)
        }
    }
}
fun scoreFats(fatsPercent: Double): Double {
    return when {
        fatsPercent in 90.0..110.0 -> 1.0
        fatsPercent < 90.0 -> (fatsPercent / 90.0).coerceIn(0.0, 1.0)
        else -> {
            val overshoot = fatsPercent - 110.0
            val penalty = exp(overshoot / 25.0) - 1.0
            (1.0 - penalty).coerceIn(0.0, 1.0)
        }
    }
}

fun scoreCalories(kcalPercent: Double): Double {
    return when {
        kcalPercent in 95.0..105.0 -> 1.0
        kcalPercent < 95.0 -> (kcalPercent / 95.0).coerceIn(0.0, 1.0)
        else -> {
            val overshoot = kcalPercent - 105.0
            val penalty = exp(overshoot / 25.0) - 1.0
            (1.0 - penalty).coerceIn(0.0, 1.0)
        }
    }
}


fun macroScoreAlgorithm(protein: Double, carbs: Double, fats: Double): Int {

    val proteinScore = scoreProtein(protein)
    val carbScore = scoreCarbs(carbs)
    val fatScore = scoreFats(fats)

    val average = (proteinScore + carbScore + fatScore) / 3.0
    return (average * 100).roundToInt()
}
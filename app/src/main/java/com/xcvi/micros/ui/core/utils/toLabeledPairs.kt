package com.xcvi.micros.ui.core.utils
import com.xcvi.micros.domain.model.food.*
import android.content.Context
import com.xcvi.micros.R
import com.xcvi.micros.domain.utils.roundDecimals

fun Nutrients.toLabeledPairs(context: Context): List<Pair<String, String>> {
    val nutrients = this
    return listOf(
        context.getString(R.string.calories) to "${nutrients.calories} kcal",
        context.getString(R.string.protein) to "${nutrients.protein.roundDecimals()} g",
        context.getString(R.string.carbs) to "${nutrients.carbohydrates.roundDecimals()} g",
        context.getString(R.string.fats) to "${nutrients.fats.roundDecimals()} g",
        context.getString(R.string.saturated_fats) to "${nutrients.saturatedFats.roundDecimals()} g",
        context.getString(R.string.fiber) to "${nutrients.fiber.roundDecimals()} g",
        context.getString(R.string.sugars) to "${nutrients.sugars.roundDecimals()} g",
        "" to ""
    )
}


fun Minerals.toLabeledPairs(context: Context): List<Pair<String, String>> {
    val minerals = this
    return listOf(
        context.getString(R.string.potassium) to "${minerals.potassium.roundDecimals()} mg",
        context.getString(R.string.calcium) to "${minerals.calcium.roundDecimals()} mg",
        context.getString(R.string.magnesium) to "${minerals.magnesium.roundDecimals()} mg",
        context.getString(R.string.iron) to "${minerals.iron.roundDecimals()} mg",
        context.getString(R.string.sodium) to "${minerals.sodium.roundDecimals()} mg",
        "" to ""
    )
}


fun Vitamins.toLabeledPairs(context: Context): List<Pair<String, String>> {
    val vitamins = this
    return listOf(
        context.getString(R.string.vitaminA) to "${vitamins.vitaminA.roundDecimals()} μg",
        context.getString(R.string.vitaminB1) to "${vitamins.vitaminB1.roundDecimals()} mg",
        context.getString(R.string.vitaminB2) to "${vitamins.vitaminB2.roundDecimals()} mg",
        context.getString(R.string.vitaminB3) to "${vitamins.vitaminB3.roundDecimals()} mg",
        context.getString(R.string.vitaminB4) to "${vitamins.vitaminB4.roundDecimals()} mg",
        context.getString(R.string.vitaminB5) to "${vitamins.vitaminB5.roundDecimals()} mg",
        context.getString(R.string.vitaminB6) to "${vitamins.vitaminB6.roundDecimals()} mg",
        context.getString(R.string.vitaminB9) to "${vitamins.vitaminB9.roundDecimals()} mg",
        context.getString(R.string.vitaminB12) to "${vitamins.vitaminB12.roundDecimals()} μg",
        context.getString(R.string.vitaminC) to "${vitamins.vitaminC.roundDecimals()} mg",
        context.getString(R.string.vitaminD) to "${vitamins.vitaminD.roundDecimals()} μg",
        context.getString(R.string.vitaminE) to "${vitamins.vitaminE.roundDecimals()} mg",
        context.getString(R.string.vitaminK) to "${vitamins.vitaminK.roundDecimals()} μg",
        "" to ""
    )
}


fun AminoAcids.toLabeledPairs(context: Context): List<Pair<String, String>> {
    val aminoAcids = this
    return listOf(
        context.getString(R.string.histidine) to "${aminoAcids.histidine.roundDecimals()} g",
        context.getString(R.string.isoleucine) to "${aminoAcids.isoleucine.roundDecimals()} g",
        context.getString(R.string.leucine) to "${aminoAcids.leucine.roundDecimals()} g",
        context.getString(R.string.lysine) to "${aminoAcids.lysine.roundDecimals()} g",
        context.getString(R.string.methionine) to "${aminoAcids.methionine.roundDecimals()} g",
        context.getString(R.string.phenylalanine) to "${aminoAcids.phenylalanine.roundDecimals()} g",
        context.getString(R.string.threonine) to "${aminoAcids.threonine.roundDecimals()} g",
        context.getString(R.string.tryptophan) to "${aminoAcids.tryptophan.roundDecimals()} g",
        context.getString(R.string.valine) to "${aminoAcids.valine.roundDecimals()} g",
        "" to ""
    )
}

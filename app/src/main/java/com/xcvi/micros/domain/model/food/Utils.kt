package com.xcvi.micros.domain.model.food
import com.xcvi.micros.domain.utils.roundToInt


fun List<Portion>.sumNutrients(): Nutrients {
    return this.fold(Nutrients.empty()) { acc, portion ->
        acc + portion.nutrients
    }
}

fun List<Portion>.sumMinerals(): Minerals {
    return this.fold(Minerals.empty()) { acc, portion ->
        acc + portion.minerals
    }
}

fun List<Portion>.sumVitamins(): Vitamins {
    return this.fold(Vitamins.empty()) { acc, portion ->
        acc + portion.vitamins
    }
}
fun List<Portion>.sumAminos(): AminoAcids {
    return this.fold(AminoAcids.empty()) { acc, portion ->
        acc + portion.aminoAcids
    }
}



fun Food.scaleToPortion(portionAmount: Int, date: Int, meal: Int): Portion {
    val portion100g = Portion(
        barcode = barcode,
        name = name,
        amount = 100,
        nutrients = nutrients,
        minerals = minerals,
        vitamins = vitamins,
        aminoAcids = aminoAcids,
        date = date,
        meal = meal,
        isFavorite = isFavorite
    )
    return portion100g.scale(portionAmount)
}

fun Portion.scale(newAmount: Int): Portion {
    fun scaleValue(value: Double) = if (this.amount == 0) 0.0 else (value * newAmount) / this.amount

    val scaledNutrients = Nutrients(
        calories = scaleValue(this.nutrients.calories.toDouble()).roundToInt(),
        protein = scaleValue(this.nutrients.protein),
        carbohydrates = scaleValue(this.nutrients.carbohydrates),
        fats = scaleValue(this.nutrients.fats),
        saturatedFats = scaleValue(this.nutrients.saturatedFats),
        fiber = scaleValue(this.nutrients.fiber),
        sugars = scaleValue(this.nutrients.sugars)
    )

    val scaledMinerals = Minerals(
        potassium = scaleValue(this.minerals.potassium),
        calcium = scaleValue(this.minerals.calcium),
        magnesium = scaleValue(this.minerals.magnesium),
        iron = scaleValue(this.minerals.iron),
        sodium = scaleValue(this.minerals.sodium),
        zinc = scaleValue(this.minerals.zinc),
        fluoride = scaleValue(this.minerals.fluoride),
        iodine = scaleValue(this.minerals.iodine),
        phosphorus = scaleValue(this.minerals.phosphorus),
        manganese = scaleValue(this.minerals.manganese),
        selenium = scaleValue(this.minerals.selenium)
    )

    val scaledVitamins = Vitamins(
        vitaminA = scaleValue(this.vitamins.vitaminA),
        vitaminB1 = scaleValue(this.vitamins.vitaminB1),
        vitaminB2 = scaleValue(this.vitamins.vitaminB2),
        vitaminB3 = scaleValue(this.vitamins.vitaminB3),
        vitaminB4 = scaleValue(this.vitamins.vitaminB4),
        vitaminB5 = scaleValue(this.vitamins.vitaminB5),
        vitaminB6 = scaleValue(this.vitamins.vitaminB6),
        vitaminB9 = scaleValue(this.vitamins.vitaminB9),
        vitaminB12 = scaleValue(this.vitamins.vitaminB12),
        vitaminC = scaleValue(this.vitamins.vitaminC),
        vitaminD = scaleValue(this.vitamins.vitaminD),
        vitaminE = scaleValue(this.vitamins.vitaminE),
        vitaminK = scaleValue(this.vitamins.vitaminK)
    )

    val scaledAminoAcids = AminoAcids(
        histidine = scaleValue(this.aminoAcids.histidine),
        isoleucine = scaleValue(this.aminoAcids.isoleucine),
        leucine = scaleValue(this.aminoAcids.leucine),
        lysine = scaleValue(this.aminoAcids.lysine),
        methionine = scaleValue(this.aminoAcids.methionine),
        phenylalanine = scaleValue(this.aminoAcids.phenylalanine),
        threonine = scaleValue(this.aminoAcids.threonine),
        tryptophan = scaleValue(this.aminoAcids.tryptophan),
        valine = scaleValue(this.aminoAcids.valine)
    )

    return this.copy(
        amount = newAmount,
        nutrients = scaledNutrients,
        minerals = scaledMinerals,
        vitamins = scaledVitamins,
        aminoAcids = scaledAminoAcids
    )
}

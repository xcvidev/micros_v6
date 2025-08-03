package com.xcvi.micros.domain.model.food
import com.xcvi.micros.domain.utils.roundDecimals
import com.xcvi.micros.domain.utils.roundToInt


fun List<Portion>.sumNutrients(): Nutrients {
    return this.fold(Nutrients.empty()) { acc, portion ->
        acc + portion.food.nutrients
    }
}

fun List<Portion>.sumMinerals(): Minerals {
    return this.fold(Minerals.empty()) { acc, portion ->
        acc + portion.food.minerals
    }
}

fun List<Portion>.sumVitamins(): Vitamins {
    return this.fold(Vitamins.empty()) { acc, portion ->
        acc + portion.food.vitamins
    }
}
fun List<Portion>.sumAminos(): AminoAcids {
    return this.fold(AminoAcids.empty()) { acc, portion ->
        acc + portion.food.aminoAcids
    }
}



fun Food.scaleToPortion(portionAmount: Int, date: Int, meal: Int): Portion {
    val portion100g = Portion(
        date = date,
        amount = 100,
        meal = meal,
        food = this
    )
    return portion100g.scale(portionAmount)
}

fun Nutrients.scale(oldAmount: Int, newAmount: Int): Nutrients {
    fun scaleValue(value: Double) = if (oldAmount == 0) 0.0 else {
        ((value * newAmount) / oldAmount).roundDecimals()
    }
    return Nutrients(
        calories = scaleValue( calories.toDouble()).roundToInt(),
        protein = scaleValue( protein),
        carbohydrates = scaleValue( carbohydrates),
        fats = scaleValue( fats),
        saturatedFats = scaleValue( saturatedFats),
        fiber = scaleValue( fiber),
        sugars = scaleValue( sugars)
    )
}

fun Minerals.scale(oldAmount: Int, newAmount: Int): Minerals {
    fun scaleValue(value: Double) = if (oldAmount == 0) 0.0 else {
        ((value * newAmount) / oldAmount).roundDecimals()
    }
    return Minerals(
        potassium = scaleValue( potassium),
        calcium = scaleValue( calcium),
        magnesium = scaleValue( magnesium),
        iron = scaleValue( iron),
        sodium = scaleValue( sodium),
        zinc = scaleValue( zinc),
        fluoride = scaleValue( fluoride),
        iodine = scaleValue( iodine),
        phosphorus = scaleValue( phosphorus),
        manganese = scaleValue( manganese),
        selenium = scaleValue( selenium)
    )
}

fun Vitamins.scale(oldAmount: Int, newAmount: Int): Vitamins {
    fun scaleValue(value: Double) = if (oldAmount == 0) 0.0 else {
        ((value * newAmount) / oldAmount).roundDecimals()
    }
    return Vitamins(
        vitaminA = scaleValue( vitaminA),
        vitaminB1 = scaleValue( vitaminB1),
        vitaminB2 = scaleValue( vitaminB2),
        vitaminB3 = scaleValue( vitaminB3),
        vitaminB4 = scaleValue( vitaminB4),
        vitaminB5 = scaleValue( vitaminB5),
        vitaminB6 = scaleValue( vitaminB6),
        vitaminB9 = scaleValue( vitaminB9),
        vitaminB12 = scaleValue( vitaminB12),
        vitaminC = scaleValue( vitaminC),
        vitaminD = scaleValue( vitaminD),
        vitaminE = scaleValue( vitaminE),
        vitaminK = scaleValue( vitaminK)
    )
}

fun AminoAcids.scale(oldAmount: Int, newAmount: Int): AminoAcids {
    fun scaleValue(value: Double) = if (oldAmount == 0) 0.0 else {
        ((value * newAmount) / oldAmount).roundDecimals()
    }
    return AminoAcids(
        histidine = scaleValue(histidine),
        isoleucine = scaleValue(isoleucine),
        leucine = scaleValue(leucine),
        lysine = scaleValue(lysine),
        methionine = scaleValue(methionine),
        phenylalanine = scaleValue(phenylalanine),
        threonine = scaleValue(threonine),
        tryptophan = scaleValue(tryptophan),
        valine = scaleValue(valine)
    )
}

fun Portion.scale(newAmount: Int): Portion {
    fun scaleValue(value: Double) = if (this.amount == 0) 0.0 else {
        ((value * newAmount) / this.amount).roundDecimals()
    }

    val scaledNutrients = Nutrients(
        calories = scaleValue(this.food.nutrients.calories.toDouble()).roundToInt(),
        protein = scaleValue(this.food.nutrients.protein),
        carbohydrates = scaleValue(this.food.nutrients.carbohydrates),
        fats = scaleValue(this.food.nutrients.fats),
        saturatedFats = scaleValue(this.food.nutrients.saturatedFats),
        fiber = scaleValue(this.food.nutrients.fiber),
        sugars = scaleValue(this.food.nutrients.sugars)
    )

    val scaledMinerals = Minerals(
        potassium = scaleValue(this.food.minerals.potassium),
        calcium = scaleValue(this.food.minerals.calcium),
        magnesium = scaleValue(this.food.minerals.magnesium),
        iron = scaleValue(this.food.minerals.iron),
        sodium = scaleValue(this.food.minerals.sodium),
        zinc = scaleValue(this.food.minerals.zinc),
        fluoride = scaleValue(this.food.minerals.fluoride),
        iodine = scaleValue(this.food.minerals.iodine),
        phosphorus = scaleValue(this.food.minerals.phosphorus),
        manganese = scaleValue(this.food.minerals.manganese),
        selenium = scaleValue(this.food.minerals.selenium)
    )

    val scaledVitamins = Vitamins(
        vitaminA = scaleValue(this.food.vitamins.vitaminA),
        vitaminB1 = scaleValue(this.food.vitamins.vitaminB1),
        vitaminB2 = scaleValue(this.food.vitamins.vitaminB2),
        vitaminB3 = scaleValue(this.food.vitamins.vitaminB3),
        vitaminB4 = scaleValue(this.food.vitamins.vitaminB4),
        vitaminB5 = scaleValue(this.food.vitamins.vitaminB5),
        vitaminB6 = scaleValue(this.food.vitamins.vitaminB6),
        vitaminB9 = scaleValue(this.food.vitamins.vitaminB9),
        vitaminB12 = scaleValue(this.food.vitamins.vitaminB12),
        vitaminC = scaleValue(this.food.vitamins.vitaminC),
        vitaminD = scaleValue(this.food.vitamins.vitaminD),
        vitaminE = scaleValue(this.food.vitamins.vitaminE),
        vitaminK = scaleValue(this.food.vitamins.vitaminK)
    )

    val scaledAminoAcids = AminoAcids(
        histidine = scaleValue(this.food.aminoAcids.histidine),
        isoleucine = scaleValue(this.food.aminoAcids.isoleucine),
        leucine = scaleValue(this.food.aminoAcids.leucine),
        lysine = scaleValue(this.food.aminoAcids.lysine),
        methionine = scaleValue(this.food.aminoAcids.methionine),
        phenylalanine = scaleValue(this.food.aminoAcids.phenylalanine),
        threonine = scaleValue(this.food.aminoAcids.threonine),
        tryptophan = scaleValue(this.food.aminoAcids.tryptophan),
        valine = scaleValue(this.food.aminoAcids.valine)
    )

    return this.copy(
        amount = newAmount,
        date = date,
        meal = meal,
        food = this.food.copy(
            nutrients = scaledNutrients,
            minerals = scaledMinerals,
            vitamins = scaledVitamins,
            aminoAcids = scaledAminoAcids
        )
    )
}

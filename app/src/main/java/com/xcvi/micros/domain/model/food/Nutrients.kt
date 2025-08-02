package com.xcvi.micros.domain.model.food



data class Macros(
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fats: Double = 0.0
){
    companion object{ fun empty() = Macros(0, 0.0, 0.0, 0.0) }
    fun isEmpty() = calories == 0 && protein == 0.0 && carbohydrates == 0.0 && fats == 0.0
    fun isNotEmpty() = !isEmpty()
}


data class Nutrients(
    val calories: Int,
    val protein: Double,
    val carbohydrates: Double,
    val fats: Double,
    val saturatedFats: Double,
    val fiber: Double,
    val sugars: Double,
) {
    operator fun minus(other: Nutrients) = Nutrients(
        calories - other.calories,
        protein - other.protein,
        carbohydrates - other.carbohydrates,
        fats - other.fats,
        saturatedFats - other.saturatedFats,
        fiber - other.fiber,
        sugars - other.sugars
    )
    operator fun plus(other: Nutrients) = Nutrients(
        calories + other.calories,
        protein + other.protein,
        carbohydrates + other.carbohydrates,
        fats + other.fats,
        saturatedFats + other.saturatedFats,
        fiber + other.fiber,
        sugars + other.sugars
    )

    companion object{ fun empty() = Nutrients(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) }
}
data class Minerals(
    val potassium: Double,
    val calcium: Double,
    val magnesium: Double,
    val iron: Double,
    val sodium: Double,
    val zinc: Double,
    val fluoride: Double,
    val iodine: Double,
    val phosphorus: Double,
    val manganese: Double,
    val selenium: Double,
) {
    operator fun plus(other: Minerals) = Minerals(
        potassium + other.potassium,
        calcium + other.calcium,
        magnesium + other.magnesium,
        iron + other.iron,
        sodium + other.sodium,
        zinc + other.zinc,
        fluoride + other.fluoride,
        iodine + other.iodine,
        phosphorus + other.phosphorus,
        manganese + other.manganese,
        selenium + other.selenium
    )

    operator fun minus(other: Minerals) = Minerals(
        potassium - other.potassium,
        calcium - other.calcium,
        magnesium - other.magnesium,
        iron - other.iron,
        sodium - other.sodium,
        zinc - other.zinc,
        fluoride - other.fluoride,
        iodine - other.iodine,
        phosphorus - other.phosphorus,
        manganese - other.manganese,
        selenium - other.selenium
    )

    companion object {
        fun empty() = Minerals(
            0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )
    }
}


data class Vitamins(
    val vitaminA: Double,
    val vitaminB1: Double,
    val vitaminB2: Double,
    val vitaminB4: Double,
    val vitaminB3: Double,
    val vitaminB5: Double,
    val vitaminB6: Double,
    val vitaminB9: Double,
    val vitaminB12: Double,
    val vitaminC: Double,
    val vitaminD: Double,
    val vitaminE: Double,
    val vitaminK: Double,
) {

    fun vitaminBTotal() = vitaminB1 + vitaminB2 + vitaminB3 + vitaminB4 + vitaminB5 + vitaminB6 + vitaminB9/1000 + vitaminB12/1000

    operator fun plus(other: Vitamins) = Vitamins(
        vitaminA + other.vitaminA,
        vitaminB1 + other.vitaminB1,
        vitaminB2 + other.vitaminB2,
        vitaminB3 + other.vitaminB3,
        vitaminB4 + other.vitaminB4,
        vitaminB5 + other.vitaminB5,
        vitaminB6 + other.vitaminB6,
        vitaminB9 + other.vitaminB9,
        vitaminB12 + other.vitaminB12,
        vitaminC + other.vitaminC,
        vitaminD + other.vitaminD,
        vitaminE + other.vitaminE,
        vitaminK + other.vitaminK
    )
    operator fun minus(other: Vitamins) = Vitamins(
        vitaminA - other.vitaminA,
        vitaminB1 + other.vitaminB1,
        vitaminB2 + other.vitaminB2,
        vitaminB3 + other.vitaminB3,
        vitaminB4 + other.vitaminB4,
        vitaminB5 + other.vitaminB5,
        vitaminB6 + other.vitaminB6,
        vitaminB9 + other.vitaminB9,
        vitaminB12 + other.vitaminB12,
        vitaminC - other.vitaminC,
        vitaminD - other.vitaminD,
        vitaminE - other.vitaminE,
        vitaminK - other.vitaminK
    )
    companion object{ fun empty() = Vitamins(0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        , 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) }
}

data class AminoAcids(
    val histidine: Double,
    val isoleucine: Double,
    val leucine: Double,
    val lysine: Double,
    val methionine: Double,
    val phenylalanine: Double,
    val threonine: Double,
    val tryptophan: Double,
    val valine: Double,
) {
    operator fun plus(other: AminoAcids) = AminoAcids(
        histidine + other.histidine,
        isoleucine + other.isoleucine,
        leucine + other.leucine,
        lysine + other.lysine,
        methionine + other.methionine,
        phenylalanine + other.phenylalanine,
        threonine + other.threonine,
        tryptophan + other.tryptophan,
        valine + other.valine
    )

    operator fun minus(other: AminoAcids) = AminoAcids(
        histidine - other.histidine,
        isoleucine - other.isoleucine,
        leucine - other.leucine,
        lysine - other.lysine,
        methionine - other.methionine,
        phenylalanine - other.phenylalanine,
        threonine - other.threonine,
        tryptophan - other.tryptophan,
        valine - other.valine
    )
    companion object{ fun empty() = AminoAcids(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) }
}
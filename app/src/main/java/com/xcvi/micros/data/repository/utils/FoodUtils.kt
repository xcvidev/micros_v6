package com.xcvi.micros.data.repository.utils

import com.xcvi.micros.data.source.local.entity.food.FoodEntity
import com.xcvi.micros.data.source.remote.dto.EnhancedDTO
import com.xcvi.micros.data.source.remote.dto.ScanProductDTO
import com.xcvi.micros.data.source.remote.dto.SearchProductDTO
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.utils.roundToInt
import com.xcvi.micros.domain.utils.toAscii


fun SearchProductDTO.toEntity(): FoodEntity {
    val brands = this.brands.joinToString(", ")
    val (tag, count) = getTag(this.name, brands)
    val displayName = getDisplayName(this.name, brands)
    val sodium = if (this.nutriments.sodium_100g <= 0.0) {
        (this.nutriments.salt_100g * 400)
    } else {
        (this.nutriments.sodium_100g * 1000)
    }
    val minerals = Minerals.empty().copy(sodium = sodium.takeIf { it > 0.0 } ?: 0.0)
    val vitamins = Vitamins.empty()
    val aminoAcids = AminoAcids.empty()
    return FoodEntity(
        barcode = barcode,
        name = displayName,
        isFavorite = false,
        isRecent = false,
        isAI = false,
        tag = tag,
        tagwordcount = count,
        calories = this.nutriments.kcal.takeIf { it > 0.0 } ?: 0.0,
        protein = this.nutriments.proteins_100g.takeIf { it > 0.0 } ?: 0.0,
        carbohydrates = this.nutriments.carbohydrates_100g.takeIf { it > 0.0 } ?: 0.0,
        fats = this.nutriments.fat_100g.takeIf { it > 0.0 } ?: 0.0,
        saturatedFats = this.nutriments.saturated_fat_100g.takeIf { it > 0.0 } ?: 0.0,
        fiber = this.nutriments.fiber_100g.takeIf { it > 0.0 } ?: 0.0,
        sugars = this.nutriments.sugars_100g.takeIf { it > 0.0 } ?: 0.0,
        potassium = minerals.potassium.takeIf { it > 0.0 } ?: 0.0,
        sodium = minerals.sodium.takeIf { it > 0.0 } ?: 0.0,

        calcium = minerals.calcium,
        iron = minerals.iron,
        magnesium = minerals.magnesium,
        zinc = minerals.zinc,
        fluoride = minerals.fluoride,
        iodine = minerals.iodine,
        phosphorus = minerals.phosphorus,
        manganese = minerals.manganese,
        selenium = minerals.selenium,
        vitaminA = vitamins.vitaminA,
        vitaminB1 = vitamins.vitaminB1,
        vitaminB2 = vitamins.vitaminB2,
        vitaminB3 = vitamins.vitaminB3,
        vitaminB4 = vitamins.vitaminB4,
        vitaminB5 = vitamins.vitaminB5,
        vitaminB6 = vitamins.vitaminB6,
        vitaminB9 = vitamins.vitaminB9,
        vitaminB12 = vitamins.vitaminB12,
        vitaminC = vitamins.vitaminC,
        vitaminD = vitamins.vitaminD,
        vitaminE = vitamins.vitaminE,
        vitaminK = vitamins.vitaminK,
        histidine = aminoAcids.histidine,
        isoleucine = aminoAcids.isoleucine,
        leucine = aminoAcids.leucine,
        lysine = aminoAcids.lysine,
        methionine = aminoAcids.methionine,
        phenylalanine = aminoAcids.phenylalanine,
        threonine = aminoAcids.threonine,
        tryptophan = aminoAcids.tryptophan,
        valine = aminoAcids.valine

    )
}

fun EnhancedDTO.mergeToFood(food: FoodEntity, newBarcode: String): FoodEntity {
    return food.copy(
        barcode = newBarcode,
        isAI = true,
        isRecent = true,
        potassium = minerals.potassium,
        calcium = minerals.calcium,
        magnesium = minerals.magnesium,
        iron = minerals.iron,
        sodium = minerals.sodium,
        zinc = minerals.zinc,
        fluoride = minerals.fluoride,
        iodine = minerals.iodine,
        phosphorus = minerals.phosphorus,
        manganese = minerals.manganese,
        selenium = minerals.selenium,
        vitaminA = vitamins.vitaminA,
        vitaminB1 = vitamins.vitaminB1,
        vitaminB2 = vitamins.vitaminB2,
        vitaminB3 = vitamins.vitaminB3,
        vitaminB4 = vitamins.vitaminB4,
        vitaminB5 = vitamins.vitaminB5,
        vitaminB6 = vitamins.vitaminB6,
        vitaminB9 = vitamins.vitaminB9,
        vitaminB12 = vitamins.vitaminB12,
        vitaminC = vitamins.vitaminC,
        vitaminD = vitamins.vitaminD,
        vitaminE = vitamins.vitaminE,
        vitaminK = vitamins.vitaminK,
        /** The AI always over estimates these **/
        histidine = aminoAcids.histidine * 0.85,
        isoleucine = aminoAcids.isoleucine * 0.85,
        leucine = aminoAcids.leucine * 0.85,
        lysine = aminoAcids.lysine * 0.85,
        methionine = aminoAcids.methionine * 0.85,
        phenylalanine = aminoAcids.phenylalanine * 0.85,
        threonine = aminoAcids.threonine * 0.85,
        tryptophan = aminoAcids.tryptophan * 0.85,
        valine = aminoAcids.valine * 0.85
    )
}


fun FoodEntity.toModel(): Food {
    return Food(
        barcode = barcode,
        name = name,
        isFavorite = isFavorite,
        isRecent = isRecent,
        isAI = isAI,

        nutrients = Nutrients(
            calories = calories.roundToInt(),
            protein = protein,
            carbohydrates = carbohydrates,
            fats = fats,
            saturatedFats = saturatedFats,
            fiber = fiber,
            sugars = sugars
        ),
        minerals = Minerals(
            calcium = calcium,
            iron = iron,
            magnesium = magnesium,
            potassium = potassium,
            sodium = sodium,
            zinc = zinc,
            fluoride = fluoride,
            iodine = iodine,
            phosphorus = phosphorus,
            manganese = manganese,
            selenium = selenium,
        ),
        vitamins = Vitamins(
            vitaminA = vitaminA,
            vitaminB1 = vitaminB1,
            vitaminB2 = vitaminB2,
            vitaminB3 = vitaminB3,
            vitaminB4 = vitaminB4,
            vitaminB5 = vitaminB5,
            vitaminB6 = vitaminB6,
            vitaminB9 = vitaminB9,
            vitaminB12 = vitaminB12,
            vitaminC = vitaminC,
            vitaminD = vitaminD,
            vitaminE = vitaminE,
            vitaminK = vitaminK
        ),
        aminoAcids = AminoAcids(
            histidine = histidine,
            isoleucine = isoleucine,
            leucine = leucine,
            lysine = lysine,
            methionine = methionine,
            phenylalanine = phenylalanine,
            threonine = threonine,
            tryptophan = tryptophan,
            valine = valine
        )
    )
}


fun Food.toEntity(): FoodEntity {
    val (tag, count) = getTag(this.name, "")
    return FoodEntity(
        barcode = barcode,
        name = name,
        isFavorite = isFavorite,
        isRecent = isRecent,
        isAI = isAI,
        tag = tag,
        tagwordcount = count,
        calories = nutrients.calories.toDouble(),
        protein = nutrients.protein,
        carbohydrates = nutrients.carbohydrates,
        fats = nutrients.fats,
        saturatedFats = nutrients.saturatedFats,
        fiber = nutrients.fiber,
        sugars = nutrients.sugars,
        calcium = minerals.calcium,
        iron = minerals.iron,
        magnesium = minerals.magnesium,
        potassium = minerals.potassium,
        sodium = minerals.sodium,
        zinc = minerals.zinc,
        fluoride = minerals.fluoride,
        iodine = minerals.iodine,
        phosphorus = minerals.phosphorus,
        manganese = minerals.manganese,
        selenium = minerals.selenium,
        vitaminA = vitamins.vitaminA,
        vitaminB1 = vitamins.vitaminB1,
        vitaminB2 = vitamins.vitaminB2,
        vitaminB3 = vitamins.vitaminB3,
        vitaminB4 = vitamins.vitaminB4,
        vitaminB5 = vitamins.vitaminB5,
        vitaminB6 = vitamins.vitaminB6,
        vitaminB9 = vitamins.vitaminB9,
        vitaminB12 = vitamins.vitaminB12,
        vitaminC = vitamins.vitaminC,
        vitaminD = vitamins.vitaminD,
        vitaminE = vitamins.vitaminE,
        vitaminK = vitamins.vitaminK,
        histidine = aminoAcids.histidine,
        isoleucine = aminoAcids.isoleucine,
        leucine = aminoAcids.leucine,
        lysine = aminoAcids.lysine,
        methionine = aminoAcids.methionine,
        phenylalanine = aminoAcids.phenylalanine,
        threonine = aminoAcids.threonine,
        tryptophan = aminoAcids.tryptophan,
        valine = aminoAcids.valine
    )
}


fun ScanProductDTO.toEntity(): FoodEntity? {
    fun getValue(main: Double, fallback: Double): Double {
        return if (main >= 0) main else fallback
    }

    val n = this.nutriments
    val e = this.nutriments_estimated

    val displayName = getDisplayName(this.name, this.brands)
    val (tag, tagWordCount) = getTag(this.name, this.brands)

    if (displayName.isBlank()) return null

    return FoodEntity(
        barcode = this.barcode,
        name = displayName,
        isAI = false,
        isRecent = true,
        /** Scanned items are always recent **/
        isFavorite = false,
        tag = tag,
        tagwordcount = tagWordCount,

        calories = getValue(n.kcal, e.kcal),
        protein = getValue(n.proteins_100g, e.proteins_100g),
        carbohydrates = getValue(n.carbohydrates_100g, e.carbohydrates_100g),
        fats = getValue(n.fat_100g, e.fat_100g),
        saturatedFats = getValue(n.saturated_fat_100g, e.saturated_fat_100g),
        fiber = getValue(n.fiber_100g, e.fiber_100g),
        sugars = getValue(n.sugars_100g, e.sugars_100g),

        calcium = getValue(n.calcium_100g, e.calcium_100g) * 1000,
        iron = getValue(n.iron_100g, e.iron_100g) * 1000,
        magnesium = getValue(n.magnesium_100g, e.magnesium_100g) * 1000,
        sodium = getValue(n.sodium_100g, e.sodium_100g) * 1000,
        potassium = getValue(n.potassium_100g, e.potassium_100g) * 1000,
        zinc = getValue(n.zinc_100g, e.zinc_100g) * 1000,
        fluoride = getValue(n.fluoride, e.fluoride) * 1000,
        iodine = getValue(n.iodine_100g, e.iodine_100g) * 1000,
        phosphorus = getValue(n.phosphorus_100g, e.phosphorus_100g) * 1000,
        manganese = getValue(n.manganese_100g, e.manganese_100g) * 1000,
        selenium = getValue(n.selenium_100g, e.selenium_100g) * 1000,

        vitaminA = getValue(n.vitaminA, e.vitaminA) * 1000000,
        vitaminB1 = getValue(n.vitaminB1, e.vitaminB1) * 1000,
        vitaminB2 = getValue(n.vitaminB2, e.vitaminB2) * 1000,
        vitaminB3 = getValue(n.vitaminB3, e.vitaminB3) * 1000,
        vitaminB4 = getValue(n.vitaminB4, e.vitaminB4) * 1000,
        vitaminB5 = getValue(n.vitaminB5, e.vitaminB5) * 1000,
        vitaminB6 = getValue(n.vitaminB6, e.vitaminB6) * 1000,
        vitaminB9 = getValue(n.vitaminB9, e.vitaminB9) * 1000000,
        vitaminB12 = getValue(n.vitaminB12, e.vitaminB12) * 1000000,
        vitaminC = getValue(n.vitaminC, e.vitaminC) * 1000,
        vitaminD = getValue(n.vitaminD, e.vitaminD) * 1000000,
        vitaminE = getValue(n.vitaminE, e.vitaminE) * 1000,
        vitaminK = getValue(n.vitaminK, e.vitaminK) * 1000000,
        histidine = 0.0,
        isoleucine = 0.0,
        leucine = 0.0,
        lysine = 0.0,
        methionine = 0.0,
        phenylalanine = 0.0,
        threonine = 0.0,
        tryptophan = 0.0,
        valine = 0.0
    )
}


fun getDisplayName(name: String, brand: String): String {
    if (name.isBlank() && brand.isBlank()) return ""
    val displayName = if (name.isBlank()) {
        brand.lowercase().replaceFirstChar { it.uppercase() }
    } else {
        if (brand.isBlank()) {
            name.lowercase().replaceFirstChar { it.uppercase() }
        } else {
            "${name.lowercase().replaceFirstChar { it.uppercase() }} (${brand})"
        }
    }

    return displayName
}

fun getTag(name: String, brand: String): Pair<String, Int> {
    val displayName = getDisplayName(name = name, brand = brand)
    val tag = displayName.toAscii()
    val count = tag.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    return Pair(tag, count)
}

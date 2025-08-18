package com.xcvi.micros.data.repository.utils

import com.xcvi.micros.data.source.local.entity.food.FoodEntity
import com.xcvi.micros.data.source.local.entity.message.FoodItemEntity
import com.xcvi.micros.data.source.local.entity.message.MessageEntity
import com.xcvi.micros.data.source.local.entity.message.relations.MessageWithFoods
import com.xcvi.micros.data.source.remote.dto.FoodDTO
import com.xcvi.micros.data.source.remote.dto.MessageDTO
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.message.FoodItem
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.domain.utils.roundToInt


fun MessageWithFoods.toModel(): Message {
    return Message(
        text = message.text,
        fromUser = message.fromUser,
        foodItems = suggestions.map { it.toPortion() },
        timestamp = message.timestamp
    )
}

fun List<FoodDTO>.toEntity(aiMessageTimestamp: Long): List<FoodItemEntity> {
    return this.map{
        FoodItemEntity(
            id = "${aiMessageTimestamp}_${it.name}",
            messageTimestamp = aiMessageTimestamp,
            name = it.name,
            amountInGrams = it.weightInGrams,
            calories = it.protein * 4 + it.carbohydrates * 4 + it.fats * 9,
            protein = it.protein,
            carbohydrates = it.carbohydrates,
            fats = it.fats,
            saturatedFats = it.saturatedFats,
            sodium = it.sodium,
            potassium = it.potassium,
            sugars = it.sugars,
            fiber = it.fiber,
            calcium = it.calcium,
            iron = it.iron,
            magnesium = it.magnesium,
            vitaminA = it.vitaminA,
            vitaminB1 = it.vitaminB1,
            vitaminB2 = it.vitaminB2,
            vitaminB3 = it.vitaminB3,
            vitaminB4 = it.vitaminB4,
            vitaminB5 = it.vitaminB5,
            vitaminB6 = it.vitaminB6,
            vitaminB9 = it.vitaminB9,
            vitaminB12 = it.vitaminB12,
            vitaminC = it.vitaminC,
            vitaminD = it.vitaminD,
            vitaminE = it.vitaminE,
            vitaminK = it.vitaminK,
            histidine = it.histidine,
            leucine = it.leucine,
            lysine = it.lysine,
            methionine = it.methionine,
            phenylalanine = it.phenylalanine,
            threonine = it.threonine,
            tryptophan = it.tryptophan,
            valine = it.valine,
            isoleucine = it.isoleucine
        )
    }
}

fun FoodItemEntity.toModel(): FoodItem {
    return FoodItem(
        id = id,
        name = name,
        amount = amountInGrams.toInt(),
        nutrients = Nutrients(
            calories = calories.roundToInt(),
            protein = protein,
            carbohydrates = carbohydrates,
            fats = fats,
            saturatedFats = saturatedFats,
            fiber = fiber,
            sugars = sugars
        ),
        minerals = Minerals.empty().copy(
            potassium = potassium,
            sodium = sodium,
            calcium = calcium,
            iron = iron,
            magnesium = magnesium
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

fun FoodItemEntity.toPortion(): Portion {
    val food = Food(
        barcode = name,
        name = name,
        nutrients = Nutrients(
            calories = calories.roundToInt(),
            protein = protein,
            carbohydrates = carbohydrates,
            fats = fats,
            saturatedFats = saturatedFats,
            fiber = fiber,
            sugars = sugars
        ),
        minerals = Minerals.empty().copy(
            potassium = potassium,
            sodium = sodium,
            calcium = calcium,
            iron = iron,
            magnesium = magnesium
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
        ),
        isAI = true,
        isRecent = true,
        isFavorite = false
    )
    return Portion(
        food = food,
        amount = amountInGrams.roundToInt(),
        date = getToday(),
        meal = 1
    )
}

/**
 * Converts suggestion entity to food entity for caching. Uses tag as barcode.
 */
fun FoodItemEntity.toFoodEntity(): FoodEntity {
    val ratio = if (amountInGrams > 0) amountInGrams / 100.0 else 1.0
    val foodEntity = Food(
        barcode = name,
        isAI = true,
        isRecent = true,
        name = name,
        isFavorite = false,
        nutrients = Nutrients(
            calories =( calories / ratio).roundToInt(),
            protein = protein / ratio,
            carbohydrates = carbohydrates / ratio,
            fats = fats / ratio,
            saturatedFats = saturatedFats / ratio,
            fiber = fiber / ratio,
            sugars =sugars / ratio
        ),
        minerals = Minerals.empty().copy(
            potassium = potassium / ratio,
            sodium = sodium / ratio,
            calcium = calcium / ratio,
            iron = iron / ratio,
            magnesium = magnesium / ratio,
        ),
        vitamins = Vitamins(
            vitaminA = vitaminA / ratio,
            vitaminB1 = vitaminB1/ ratio,
            vitaminB2 = vitaminB2 / ratio,
            vitaminB3 = vitaminB3 / ratio,
            vitaminB4 = vitaminB4 / ratio,
            vitaminB5 = vitaminB5 / ratio,
            vitaminB6 = vitaminB6 / ratio,
            vitaminB9 = vitaminB9 / ratio,
            vitaminB12 = vitaminB12 / ratio,
            vitaminC = vitaminC / ratio,
            vitaminD = vitaminD / ratio,
            vitaminE = vitaminE / ratio,
            vitaminK = vitaminK / ratio,
        ),
        aminoAcids = AminoAcids(
            histidine = histidine / ratio,
            isoleucine = isoleucine / ratio,
            leucine = leucine / ratio,
            lysine = lysine / ratio,
            methionine = methionine / ratio,
            phenylalanine = phenylalanine / ratio,
            threonine = threonine / ratio,
            tryptophan = tryptophan / ratio,
            valine = valine / ratio,
        )
    ).toEntity()

    return foodEntity
}



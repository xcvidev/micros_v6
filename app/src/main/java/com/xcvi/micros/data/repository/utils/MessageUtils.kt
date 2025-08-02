package com.xcvi.micros.data.repository.utils

import com.xcvi.micros.data.source.local.entity.food.FoodEntity
import com.xcvi.micros.data.source.local.entity.message.FoodItemEntity
import com.xcvi.micros.data.source.local.entity.message.relations.MessageWithFoods
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.message.FoodItem
import com.xcvi.micros.domain.utils.roundToInt


fun MessageWithFoods.toModel(): Message {
    return Message(
        text = message.text,
        fromUser = message.fromUser,
        foodItems = suggestions.map { it.toModel() },
        timestamp = message.timestamp
    )
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
            sodium = sodium
        )
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
            sodium = sodium / ratio
        ),
        vitamins = Vitamins.empty(),
        aminoAcids = AminoAcids.empty()
    ).toEntity()

    return foodEntity
}



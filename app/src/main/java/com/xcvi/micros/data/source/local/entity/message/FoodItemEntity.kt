package com.xcvi.micros.data.source.local.entity.message

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodItemEntity(
    @PrimaryKey
    val id: String,

    val messageTimestamp: Long,

    val name: String,
    val amountInGrams: Double,

    val calories: Double,

    val protein: Double,
    val carbohydrates: Double,
    val fats: Double,

    val saturatedFats: Double,
    val fiber: Double,
    val sugars: Double,

    val calcium: Double,
    val iron: Double,
    val magnesium: Double,
    val potassium: Double,
    val sodium: Double,

    val vitaminA: Double, //mcg
    val vitaminB1: Double, //mg
    val vitaminB2: Double, //mg
    val vitaminB3: Double, //mg
    val vitaminB4: Double, //mg
    val vitaminB5: Double, //mg
    val vitaminB6: Double, //mg
    val vitaminB9: Double, //mg
    val vitaminB12: Double, //mcg
    val vitaminC: Double, //mg
    val vitaminD: Double, //mcg
    val vitaminE: Double, //mg
    val vitaminK: Double, //mcg


    val histidine: Double,
    val isoleucine: Double,
    val leucine: Double,
    val lysine: Double,
    val methionine: Double,
    val phenylalanine: Double,
    val threonine: Double,
    val tryptophan: Double,
    val valine: Double
)
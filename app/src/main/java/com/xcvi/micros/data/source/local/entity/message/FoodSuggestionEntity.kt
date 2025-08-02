package com.xcvi.micros.data.source.local.entity.message

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodSuggestionEntity(
    @PrimaryKey
    val id: String,

    val messageTimestamp: Long, //FK

    val name: String,
    val barcode: String,

    val amountInGrams: Double,

    val calories: Double,

    val protein: Double,
    val carbohydrates: Double,
    val fats: Double,

    val saturatedFats: Double = 0.0,
    val fiber: Double = 0.0,
    val sugars: Double = 0.0,

    val sodium: Double = 0.0,
    val potassium: Double = 0.0,
)
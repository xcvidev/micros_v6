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

    val sodium: Double,
    val potassium: Double,
)
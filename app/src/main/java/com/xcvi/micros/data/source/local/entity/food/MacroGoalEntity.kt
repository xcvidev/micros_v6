package com.xcvi.micros.data.source.local.entity.food

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MacroGoalEntity(
    @PrimaryKey
    val date: Int,
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fats: Double
)
package com.xcvi.micros.data.source.local.entity.weight

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeightEntity(
    @PrimaryKey val timestamp: Long,
    val weight: Double,
    val date: Int,
    val unit: String
)
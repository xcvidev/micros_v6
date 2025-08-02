package com.xcvi.micros.data.source.local.entity.food

import androidx.room.Entity

@Entity(
    primaryKeys = ["date", "meal", "barcode"]
)
data class PortionEntity(
    val date: Int, //epoch date
    val meal: Int, // meal number
    val barcode: String,
    val amount: Double
)
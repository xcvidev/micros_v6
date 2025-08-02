package com.xcvi.micros.domain.model

data class Suggestion(
    val id: String,
    val barcode: String,
    val name: String,
    val amount: Int,

    val calories: Double = 0.0,

    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fats: Double= 0.0,

    val saturatedFats: Double = 0.0,
    val fiber: Double = 0.0,
    val sugars: Double = 0.0,

    val sodium: Double = 0.0,
    val potassium: Double = 0.0,
)
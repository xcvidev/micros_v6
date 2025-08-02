package com.xcvi.micros.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    val message: String = "",
    val foods: List<FoodDTO> = emptyList()
){
    fun isNotEmpty(): Boolean {
        return message.isNotBlank() || foods.isNotEmpty()
    }
}

@Serializable
data class FoodDTO(
    val name: String = "",
    val weightInGrams: Double = 0.0,
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

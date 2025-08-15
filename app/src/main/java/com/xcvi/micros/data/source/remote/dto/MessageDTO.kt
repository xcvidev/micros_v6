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

    val calcium: Double = 0.0,
    val iron: Double = 0.0,  
    val magnesium: Double = 0.0,
    val potassium: Double = 0.0, 
    val sodium: Double = 0.0,

    val vitaminA: Double = 0.0, //mcg
    val vitaminB1: Double = 0.0, //mg
    val vitaminB2: Double = 0.0, //mg
    val vitaminB3: Double = 0.0, //mg
    val vitaminB4: Double = 0.0, //mg
    val vitaminB5: Double = 0.0, //mg
    val vitaminB6: Double = 0.0, //mg
    val vitaminB9: Double = 0.0, //mg
    val vitaminB12: Double = 0.0, //mcg
    val vitaminC: Double = 0.0, //mg
    val vitaminD: Double = 0.0, //mcg
    val vitaminE: Double = 0.0, //mg
    val vitaminK: Double = 0.0, //mcg


    val histidine: Double = 0.0,
    val isoleucine: Double = 0.0,
    val leucine: Double = 0.0,
    val lysine: Double = 0.0,
    val methionine: Double = 0.0,
    val phenylalanine: Double = 0.0,
    val threonine: Double = 0.0,
    val tryptophan: Double = 0.0,
    val valine: Double = 0.0
    
    

)

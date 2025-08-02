package com.xcvi.micros.domain.model.message

data class Message(
    val timestamp: Long,
    val text: String?,
    val fromUser: Boolean,
    val foodItems: List<FoodItem> = emptyList()
)
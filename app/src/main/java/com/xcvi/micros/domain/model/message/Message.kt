package com.xcvi.micros.domain.model.message

import com.xcvi.micros.domain.model.food.Portion

data class Message(
    val timestamp: Long,
    val text: String?,
    val fromUser: Boolean,
    val foodItems: List<Portion> = emptyList()
)
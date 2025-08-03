package com.xcvi.micros.domain.model.food

data class Portion(
    val amount: Int,
    val date: Int,
    val meal: Int,

    val food: Food
) 
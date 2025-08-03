package com.xcvi.micros.ui.navigation.destinations

import kotlinx.serialization.Serializable

@Serializable
data class MealDestination(
    val number: Int,
    val date: Int,
    val label: String
)
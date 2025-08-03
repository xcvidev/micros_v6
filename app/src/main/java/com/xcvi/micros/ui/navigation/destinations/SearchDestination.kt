package com.xcvi.micros.ui.navigation.destinations

import kotlinx.serialization.Serializable

@Serializable
data class SearchDestination(
    val mealNumber: Int,
    val date: Int,
    val mealLabel: String?,
)
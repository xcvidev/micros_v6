package com.xcvi.micros.ui.navigation.destinations
import kotlinx.serialization.Serializable

@Serializable
data class ScanDestination(
    val date: Int,
    val mealNumber: Int,
)
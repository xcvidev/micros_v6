package com.xcvi.micros.ui.screens.weight

import com.xcvi.micros.domain.model.weight.Weight

sealed interface WeightEvent {
    data object ToggleUnit : WeightEvent
    data class SetDate(val date: Int) : WeightEvent
    data class Save(val onError: () -> Unit = {}) : WeightEvent
    data class SetPickerValue(val value: Double) : WeightEvent
    data class SetDeleteWeight(val weight: Weight) : WeightEvent
    data object ConfirmDelete : WeightEvent
}
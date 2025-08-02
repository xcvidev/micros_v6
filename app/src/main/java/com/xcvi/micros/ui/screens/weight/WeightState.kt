package com.xcvi.micros.ui.screens.weight

import com.xcvi.micros.domain.model.weight.Weight
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.utils.getToday

data class WeightState(
    val initialValue: Double? = null,
    val pickerValue: Double = 0.0,
    val currentDate: Int = getToday(),
    val weights: List<Weight> = emptyList(),
    val deleteWeight: Weight? = null,
    val unit: WeightUnit = WeightUnit.kg,
)


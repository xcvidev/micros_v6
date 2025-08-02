package com.xcvi.micros.domain.model.weight

import com.xcvi.micros.domain.model.utils.DailySummary

data class WeightSummary(
    override val date: Int,
    val min: Double,
    val max: Double,
    val avg: Double,
    val label: String,
    val unit: WeightUnit
): DailySummary {
    companion object {
        fun empty() = WeightSummary(0, 0.0, 0.0, 0.0, "",unit = WeightUnit.kg)
    }
}
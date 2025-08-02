package com.xcvi.micros.data.source.local.entity.weight

data class WeightSummaryEntity(
    val date: Int,
    val min: Double,
    val max: Double,
    val avg: Double,
    val unit: String
)
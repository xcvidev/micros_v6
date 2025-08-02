package com.xcvi.micros.domain.model.weight

data class Weight(
    val weight: Double,
    val timestamp: Long,
    val date: Int,
    val unit: WeightUnit
){
    companion object{
        val empty = Weight(0.0, 0L, 0, WeightUnit.kg)
    }
}
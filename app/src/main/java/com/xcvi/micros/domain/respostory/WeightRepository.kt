package com.xcvi.micros.domain.respostory

import com.xcvi.micros.domain.model.weight.Weight
import com.xcvi.micros.domain.model.weight.WeightSummary
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface WeightRepository {

    suspend fun changeUnit(unit: WeightUnit): Response<Unit>

    suspend fun getLatest(): Response<Weight>

    suspend fun getWeights(start: Int, end: Int): Flow<List<Weight>>

    suspend fun delete(weight: Weight): Response<Unit>

    suspend fun save(value: Double, date: Int, unit: WeightUnit): Response<Unit>

    /** All SUMMARIZED By Date **/
    suspend fun getHistory(): List<WeightSummary>

}
package com.xcvi.micros.domain.usecases
import com.xcvi.micros.domain.model.weight.Weight
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.respostory.WeightRepository
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getStartOfWeek
import kotlinx.coroutines.flow.Flow

class WeightUseCases(
    private val weightRepository: WeightRepository,
) {


    suspend fun changeUnit(unit: WeightUnit): Response<Unit> {
        return weightRepository.changeUnit(unit)
    }

    suspend fun getLatest(): Response<Weight> {
        return weightRepository.getLatest()
    }

    suspend fun save(value: Double, date: Int, unit: WeightUnit): Response<Unit> {
        return weightRepository.save(value, date, unit)
    }

    suspend fun delete(weight: Weight): Response<Unit> {
        return weightRepository.delete(weight)
    }

    suspend fun getData(date: Int): Flow<List<Weight>> {
        val start = date.getStartOfWeek()
        val weights = weightRepository.getWeights(start = start, end = start + 6)
        return weights
    }

}
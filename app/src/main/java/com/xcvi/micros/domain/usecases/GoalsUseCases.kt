package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow

class GoalsUseCases(
    private val portionRepository: PortionRepository
){
    suspend fun getGoal(date: Int): Flow<MacrosSummary> {
        return portionRepository.getSummaryOfDate(date)
    }

    suspend fun updateMacroGoals(protein: Int, carbs: Int, fats: Int): Response<Unit> {
        TODO()
    }


}
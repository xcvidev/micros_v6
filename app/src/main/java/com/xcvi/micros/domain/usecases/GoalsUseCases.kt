package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.sumAminos
import com.xcvi.micros.domain.model.food.sumMinerals
import com.xcvi.micros.domain.model.food.sumNutrients
import com.xcvi.micros.domain.model.food.sumVitamins
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GoalsUseCases(
    private val portionRepository: PortionRepository
){
    fun getSummary(date: Int): Flow<MacrosSummary> {
        val summary=  portionRepository.getSummaryOfDate(date)
        val portions = portionRepository.getPortionsOfDate(date)
        return combine(summary, portions) { summary, portions ->
            MacrosSummary(
                date = date,
                actual = summary.actual,
                goal = summary.goal,
                actualMinerals = portions.sumMinerals(),
                actualNutrients = portions.sumNutrients(),
                actualVitamins = portions.sumVitamins(),
                actualAminoacids = portions.sumAminos()
            )
        }
    }


    suspend fun updateMacroGoals(protein: Int, carbs: Int, fats: Int): Response<Unit> {
        return portionRepository.saveGoals(protein, carbs, fats)

    }


}
package com.xcvi.micros.domain.usecases


import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Meal
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow


class DashboardUseCases(
    private val portionRepository: PortionRepository,
) {

    fun observeMeals(date: Int, mealNames: Map<Int, String>): Flow<List<Meal>> {
        return portionRepository.getMeals(date, mealNames)
    }

    fun observeGoal(date: Int): Flow<MacrosSummary> {
        return portionRepository.getSummaryOfDate(date)
    }

    fun observeGoals(): Flow<List<MacrosSummary>> {
        return portionRepository.observeAllSummaries()
    }


    suspend fun pasteMeal(meal: Meal, newDate: Int, newNumber: Int): Response<Unit> {
        return portionRepository.copyPortions(meal.portions, newDate, newNumber)
    }

    suspend fun deleteMeal(meal: Meal): Response<Unit> {
        return portionRepository.deletePortions(meal.date, meal.number)
    }

}
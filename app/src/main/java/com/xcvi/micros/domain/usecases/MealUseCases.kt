package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow


class MealUseCases(
    private val portionRepository: PortionRepository,
){
    fun getMealData(date: Int, meal: Int): Flow<List<Portion>> {
        return portionRepository.getPortionsOfMeal(date = date, meal = meal)

    }

    suspend fun updatePortion(
        newAmount: Int,
        date: Int,
        meal: Int,
        barcode: String,
    ): Response<Unit> {
        return portionRepository.savePortion(
            amount = newAmount,
            date = date,
            meal = meal,
            barcode = barcode
        )
    }

    suspend fun deletePortion(barcode: String, date: Int, meal: Int): Response<Unit> {
        return portionRepository.deletePortion(barcode = barcode, date = date, meal = meal)
    }
}
package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow


class MealUseCases(
    private val portionRepository: PortionRepository,
    private val foodRepository: FoodRepository,
) {
    suspend fun create(
        date: Int,
        meal: Int,
        amount: Int,
        name: String,
        minerals: Minerals,
        nutrients: Nutrients,
        vitamins: Vitamins,
        aminoAcids: AminoAcids
    ): Response<Unit> {
        val food = Food(
            barcode = name,
            name = name,
            minerals = minerals,
            nutrients = nutrients,
            vitamins = vitamins,
            aminoAcids = aminoAcids,
            isAI = false,
            isFavorite = true,
            isRecent = true
        )
        val portion = food.scaleToPortion(portionAmount = 100, date = date, meal = meal)
        when (foodRepository.create(portion.food)) {
            is Response.Error -> return Response.Error(Failure.Database)
            is Response.Success -> {
                portionRepository.savePortion(
                    amount = amount,
                    date = date,
                    meal = meal,
                    barcode = name
                )
                return Response.Success(Unit)
            }
        }
    }

    suspend fun clearMeal(date: Int, meal: Int): Response<Unit> {
        return portionRepository.deletePortions(date = date, meal = meal)
    }

    fun getMealData(date: Int, meal: Int): Flow<List<Portion>> {
        return portionRepository.getPortionsOfMeal(date = date, meal = meal)
    }

    suspend fun toggleFavorite(barcode: String): Response<Unit> {
        return foodRepository.toggleFavorite(barcode)
    }

    suspend fun enhance(barcode: String, description: String): Response<Food> {
        return foodRepository.enhance(barcode, description)
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
package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.model.food.sumAminoAcids
import com.xcvi.micros.domain.model.food.sumMinerals
import com.xcvi.micros.domain.model.food.sumNutrients
import com.xcvi.micros.domain.model.food.sumVitamins
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow


class MealUseCases(
    private val portionRepository: PortionRepository,
    private val foodRepository: FoodRepository,
) {
    suspend fun createMeal(
        date: Int,
        meal: Int,
        name: String,
        portions: List<Portion>,
    ): Response<Unit> {
        val minerals = portions.sumMinerals()
        val nutrients = portions.sumNutrients()
        val vitamins = portions.sumVitamins()
        val aminoAcids = portions.sumAminoAcids()
        val amount = portions.sumOf { it.amount }
        val customFood = Food(
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

        val customPortion = Portion(
            date = date,
            amount = amount,
            meal = meal,
            food = customFood
        )
        val food100g = customPortion.scale(100).food
        when (val res = foodRepository.create(food100g)) {
            is Response.Error -> return res
            is Response.Success -> {
                clearMeal(date = date, meal = meal)
                portionRepository.savePortion(
                    amount = amount,
                    date = date,
                    meal = meal,
                    barcode = name
                )
                return res
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
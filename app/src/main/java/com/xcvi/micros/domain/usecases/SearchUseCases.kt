package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SearchUseCases(
    private val foodRepository: FoodRepository,
    private val portionRepository: PortionRepository,
) {
    suspend fun enhance(barcode: String, description: String): Response<Food> {
        return foodRepository.enhance(barcode, description)
    }

    suspend fun toggleFavorite(barcode: String): Response<Unit> {
        return foodRepository.toggleFavorite(barcode)
    }

    suspend fun eat(
        date: Int,
        meal: Int,
        portions: Set<Portion>
    ): Response<Unit> {
        val toSave = portions.map {
            //Important for when saving recents with different dates and meal numbers
            it.copy(date = date, meal = meal)
        }
        return portionRepository.savePortions(toSave)
    }

    suspend fun getRecents(): List<Portion> {
        val portions = portionRepository.getRecents()
        val foods = foodRepository.getRecents()
            .filter{ f ->
                portions.none { p -> p.food.barcode == f.barcode }
            }
            .map { food->
                Portion(
                    date = 0,
                    meal = 0,
                    food = food,
                    amount = 100
                )
        }
        return (portions + foods).sortedByDescending { it.food.isFavorite }
    }

    suspend fun search(
        query: String,
        date: Int,
        meal: Int,
        language: String
    ): Response<List<Portion>> {
        val res = foodRepository.search(query, language)
        return when(res){
            is Response.Error -> res
            is Response.Success -> {
                val portions = res.data.map {
                    it.scaleToPortion(
                        date = date,
                        meal = meal,
                        portionAmount = 100
                    )
                }
                Response.Success(portions)
            }
        }
    }

}














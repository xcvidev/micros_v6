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

    suspend fun eat(
        date: Int,
        meal: Int,
        portions: Set<Portion>
    ): Response<Unit> {
        /**
         * Important for when saving recents
         */
        val toSave = portions.map {
            it.copy(date = date, meal = meal)
        }
        return portionRepository.savePortions(toSave)
    }

    suspend fun getRecents(): List<Portion> {
        return portionRepository.getRecents()
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














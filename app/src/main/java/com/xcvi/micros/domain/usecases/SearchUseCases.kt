package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow

class SearchUseCases(
    private val foodRepository: FoodRepository,
    private val portionRepository: PortionRepository,
) {
    suspend fun enhance(barcode: String, description: String): Response<Food> {
        return foodRepository.enhance(barcode, description)
    }

    suspend fun eat(
        portions: List<Portion>
    ): Response<Unit> {
        return portionRepository.savePortions(portions)
    }

    suspend fun getRecents(): Flow<List<Food>> {
        return foodRepository.getRecents()
    }

    suspend fun search(query: String, language: String): Response<List<Food>> {
        return foodRepository.search(query, language)
    }

}
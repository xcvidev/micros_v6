package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

class SearchUseCases(
    private val foodRepository: FoodRepository,
    private val portionRepository: PortionRepository,
) {

    suspend fun eat(
        date: Int,
        mealNumber: Int,
        barcodes: List<String>
    ) {
        portionRepository.savePortions(
            date = date,
            meal = mealNumber,
            barcodes = barcodes
        )
    }

    suspend fun getRecents(): Flow<List<Food>> {
        return foodRepository.getRecents()
    }

    suspend fun search(query: String, language: String): Response<List<Food>> {
        return foodRepository.search(query, language)
    }

}
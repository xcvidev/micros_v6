package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response

class DetailsUseCases(
    private val portionRepository: PortionRepository,
    private val foodRepository: FoodRepository,
    private val assistantRepository: MessageRepository,
){
    suspend fun enhance(barCode: String, description: String): Response<Food> {
        return foodRepository.enhance(barCode, description)
    }
}
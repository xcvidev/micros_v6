package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.respostory.PortionRepository

class DetailsUseCases(
    private val portionRepository: PortionRepository,
    private val foodRepository: FoodRepository,
    private val assistantRepository: MessageRepository,
)
package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getNow
import kotlinx.coroutines.flow.Flow

class MessageUseCases(
    private val messageRepository: MessageRepository,
    private val foodRepository: FoodRepository,
    private val portionRepository: PortionRepository

) {

    suspend fun toggleFavorite(barcode: String): Response<Unit> {
        return foodRepository.toggleFavorite(barcode)
    }

    suspend fun enhance(barcode: String, description: String): Response<Food> {
        return foodRepository.enhance(barcode, description)
    }

    suspend fun eat(
        portion: Portion
    ): Response<Unit> {
        return portionRepository.savePortions(listOf(portion))
    }

    suspend fun scan(barcode: String, date: Int, meal: Int):  Response<Portion> {
        return when(val res = foodRepository.scan(barcode)){
            is Response.Error -> Response.Error(res.error)
            is Response.Success -> {
                val portion = res.data.scaleToPortion(
                    date = date,
                    meal = meal,
                    portionAmount = 100
                )
                Response.Success(portion)
            }
        }
    }

    suspend fun askAssistant(
        userInput: String,
        language: String,
        recentMessages: List<Message>,
    ): Response<Unit> {
        return messageRepository.ask(
            userInput = userInput,
            language = language,
            recentMessages = recentMessages
        )
    }

    fun observeMessages(limit: Int, offset: Int, fromTimestamp: Long = getNow()): Flow<List<Message>> {
        return messageRepository.getMessages(
            limit = limit,
            offset = offset,
            fromTimestamp = fromTimestamp
        )
    }

    suspend fun getMessageCount(): Int {
        return messageRepository.messageCount()
    }


    suspend fun clearHistory(): Response<Unit> {
        return messageRepository.clearHistory()
    }

}
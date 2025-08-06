package com.xcvi.micros.domain.respostory

import com.xcvi.micros.domain.model.message.FoodItem
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun messageCount(): Int
    fun getMessages(limit: Int, offset: Int, fromTimestamp: Long): Flow<List<Message>>
    suspend fun getSuggestion(id: String): Response<FoodItem>
    suspend fun ask(userInput: String, language: String, recentMessages: List<Message>): Response<Unit>
    suspend fun smartSearch(userInput: String, language: String): Response<Message>
    suspend fun clearHistory(): Response<Unit>
    
}
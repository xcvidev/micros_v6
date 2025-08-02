package com.xcvi.micros.domain.usecases

import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getNow
import kotlinx.coroutines.flow.Flow

class MessageUseCases(
    private val messageRepository: MessageRepository,
) {

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
package com.xcvi.micros.data.repository

import com.xcvi.micros.data.source.local.message.MessageDao
import com.xcvi.micros.data.source.local.food.FoodDao
import com.xcvi.micros.data.source.remote.AiApi
import com.xcvi.micros.domain.model.Message
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.fetchAndCache
import com.xcvi.micros.domain.utils.getNow

class MessageRepositoryImplementation(
    private val api: AiApi,
    private val messageDao: MessageDao,
    private val foodDao: FoodDao
) {

    suspend fun ask(userInput: String, language: String, recentMessages: List<Message>): Response<Unit> {
        val query = getMessagePrompt(
            recentMessages = recentMessages,
            language = language,
            userInput = userInput
        )
        val userMessageTimestamp = getNow()
        val aiMessageTimestamp = userMessageTimestamp + 1

        val res = fetchAndCache(
            apiCall = { api.askAi(query)},
            cacheCall = {},
            dbCall = {},
            fallbackRequest = null,
            fallbackDbCall = {}
        )
        return when(res){
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> {
                /** Clean up user + assistant messages since we know the request failed **/
                messageDao.delete(aiMessageTimestamp)
                messageDao.delete(userMessageTimestamp)
                Response.Error(res.error)
            }
        }

    }


    private fun getMessagePrompt(
        recentMessages: List<Message>,
        language: String,
        userInput: String
    ): String {
        val contextBuilder = StringBuilder()
        recentMessages.forEach { message ->
            val speaker = if (message.fromUser) "<User>" else "<Assistant>"
            contextBuilder.append("$speaker ${message.text}\n")
            if (!message.fromUser && message.suggestions.isNotEmpty()) {
                contextBuilder.append("<Foods: ")
                contextBuilder.append(message.suggestions.joinToString(", ") { it.name })
                contextBuilder.append(">\n")
            }
        }
        contextBuilder.append("<User> $userInput")

        return """
            <User language: $language>
            Use the recent conversation context **only if it helps answer the current question**. Otherwise, ignore it.
            Here is the recent conversation context:
            $contextBuilder
            Now answer the last user query.
        """.trimIndent()
    }
}
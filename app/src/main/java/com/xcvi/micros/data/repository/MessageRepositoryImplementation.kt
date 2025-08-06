package com.xcvi.micros.data.repository

import android.util.Log
import com.xcvi.micros.data.repository.utils.toFoodEntity
import com.xcvi.micros.data.repository.utils.toModel
import com.xcvi.micros.data.source.local.entity.message.FoodItemEntity
import com.xcvi.micros.data.source.local.entity.message.MessageEntity
import com.xcvi.micros.data.source.local.message.MessageDao
import com.xcvi.micros.data.source.local.food.FoodDao
import com.xcvi.micros.data.source.remote.AiApi
import com.xcvi.micros.data.source.remote.MessageType
import com.xcvi.micros.domain.model.message.FoodItem
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.fetchAndCache
import com.xcvi.micros.domain.utils.getNow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MessageRepositoryImplementation(
    private val api: AiApi,
    private val messageDao: MessageDao,
    private val foodDao: FoodDao
): MessageRepository {

    override suspend fun smartSearch(
        userInput: String,
        language: String
    ): Response<Message> {
        val query = getMessagePrompt(
            recentMessages = emptyList(),
            language = language,
            userInput = userInput
        )
        val userMessageTimestamp = getNow()
        val aiMessageTimestamp = userMessageTimestamp + 1

        return fetchAndCache(
            apiCall = { api.askAi(query, MessageType.SMART_SEARCH_QUERY) },
            cacheCall = { response ->
                val userMessage = MessageEntity(
                    timestamp = userMessageTimestamp,
                    text = userInput,
                    fromUser = true
                )
                messageDao.insert(message = userMessage, suggestions = emptyList())

                val aiMessage = MessageEntity(
                    timestamp = aiMessageTimestamp,
                    text = response.message,
                    fromUser = false
                )
                val foodItems = response.foods.map {
                    FoodItemEntity(
                        id = "${aiMessageTimestamp}_${it.name}",
                        messageTimestamp = aiMessageTimestamp,
                        name = it.name,
                        amountInGrams = it.weightInGrams,
                        calories = it.protein*4 + it.carbohydrates*4 + it.fats*9,
                        protein = it.protein,
                        carbohydrates = it.carbohydrates,
                        fats = it.fats,
                        saturatedFats = it.saturatedFats,
                        sodium = it.sodium,
                        potassium = it.potassium,
                        sugars = it.sugars,
                        fiber = it.fiber,
                    )
                }
                messageDao.insert(aiMessage, foodItems)

                val foods = foodItems.map { it.toFoodEntity() }
                foodDao.upsert(foods)
            },
            dbCall = {
                messageDao.getMessageWithFoods(aiMessageTimestamp)?.toModel()
            },
            fallbackRequest = null,
            fallbackDbCall = {null}
        )
    }

    override suspend fun messageCount(): Int {
        return try{
            messageDao.messageCount()
        } catch (e: Exception) {
            Log.e("log", "messageCount: ${e.message}")
            0
        }
    }

    override fun getMessages(limit: Int, offset: Int, fromTimestamp: Long): Flow<List<Message>> {
        return messageDao.observeMessages(limit = limit, offset = offset, fromTimestamp = fromTimestamp)
            .map { list -> list.map { it.toModel() } }
            .catch {
                Log.e("log", "getMessages: ${it.message}")
                emit(emptyList())
            }
    }

    override suspend fun getSuggestion(id: String): Response<FoodItem> {
        return try {
            val suggestion = messageDao.getSuggestion(id) ?: return Response.Error(Failure.EmptyResult)
            Response.Success(suggestion.toModel())
        } catch (e: Exception) {
            Log.e("log", "getSuggestion: ${e.message}")
            Response.Error(Failure.Database)
        }
    }

    override suspend fun ask(userInput: String, language: String, recentMessages: List<Message>): Response<Unit> {
        val query = getMessagePrompt(
            recentMessages = recentMessages,
            language = language,
            userInput = userInput
        )
        val userMessageTimestamp = getNow()
        val aiMessageTimestamp = userMessageTimestamp + 1

        val res = fetchAndCache(
            apiCall = { api.askAi(query, MessageType.MESSAGE_QUERY) },
            cacheCall = { response ->
                val userMessage = MessageEntity(
                    timestamp = userMessageTimestamp,
                    text = userInput,
                    fromUser = true
                )
                messageDao.insert(message = userMessage, suggestions = emptyList())

                val aiMessage = MessageEntity(
                    timestamp = aiMessageTimestamp,
                    text = response.message,
                    fromUser = false
                )
                val foodItems = response.foods.map {
                    FoodItemEntity(
                        id = "${aiMessageTimestamp}_${it.name}",
                        messageTimestamp = aiMessageTimestamp,
                        name = it.name,
                        amountInGrams = it.weightInGrams,
                        calories = it.protein*4 + it.carbohydrates*4 + it.fats*9,
                        protein = it.protein,
                        carbohydrates = it.carbohydrates,
                        fats = it.fats,
                        saturatedFats = it.saturatedFats,
                        sodium = it.sodium,
                        potassium = it.potassium,
                        sugars = it.sugars,
                        fiber = it.fiber,
                    )
                }
                messageDao.insert(aiMessage, foodItems)

                val foods = foodItems.map { it.toFoodEntity() }
                foodDao.upsert(foods)
            },
            dbCall = {
                messageDao.getMessage(aiMessageTimestamp)
            },
            fallbackRequest = null,
            fallbackDbCall = {}
        )
        return when(res){
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> {
                /** Clean up user + assistant messages since we know the request failed **/
                messageDao.delete(aiMessageTimestamp)
                messageDao.delete(userMessageTimestamp)
                Log.e("log", "ask: ${res.error}")
                Response.Error(res.error)
            }
        }

    }

    override suspend fun clearHistory(): Response<Unit> {
        try {
            messageDao.clear()
            return Response.Success(Unit)
        } catch (e: Exception) {
            Log.e("log", "clearHistory: ${e.message}")
            return Response.Error(Failure.Database)
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
            if (!message.fromUser && message.foodItems.isNotEmpty()) {
                contextBuilder.append("<Foods: ")
                contextBuilder.append(message.foodItems.joinToString(", ") { it.food.name })
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
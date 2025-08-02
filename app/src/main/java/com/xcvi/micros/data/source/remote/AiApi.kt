package com.xcvi.micros.data.source.remote

import com.xcvi.micros.data.source.remote.dto.EnhancedDTO
import com.xcvi.micros.data.source.remote.dto.MessageDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class AiApi(
    private val jsonParser: Json,
    private val client: HttpClient
){
    suspend fun askAi(
        query: String,
    ): MessageDTO? {
        val responseJson = queryOpenAi(
            systemPrompt = MESSAGE_SYSTEM_PROMPT,
            userPrompt = query
        )
        if (responseJson.isNullOrBlank()) return null
        val dto = jsonParser.decodeFromString<MessageDTO>(responseJson)
        return dto
    }

    suspend fun enhance(name: String, ingredients: String, userDesc: String): EnhancedDTO? {
        val prompt =
            getEnhancementPrompt(name = name, ingredients = ingredients, userDesc = userDesc)
        val res = queryOpenAi(systemPrompt = GENERATE_SYSTEM_PROMPT, userPrompt = prompt)
        if (res.isNullOrBlank()) return null
        val jsonClean = res
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```").trim()
        return jsonParser.decodeFromString<EnhancedDTO>(jsonClean)
    }

    private suspend fun queryOpenAi(systemPrompt: String, userPrompt: String): String? {
        val url = "https://api.openai.com/v1/chat/completions"
        val requestBody = buildJsonObject {
            put("model", "gpt-3.5-turbo-1106")
            putJsonArray("messages") {
                addJsonObject {
                    put("role", "system")
                    put("content", systemPrompt)
                }
                addJsonObject {
                    put("role", "user")
                    put("content", userPrompt)
                }
            }
            put("temperature", 0.7)
        }
        val response: JsonObject = client.post(url) {
            body = requestBody
        }
        return response["choices"]?.jsonArray?.get(0)
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.contentOrNull
    }
}
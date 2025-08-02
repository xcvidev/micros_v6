package com.xcvi.micros.data.source.local.message

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.xcvi.micros.data.source.local.entity.message.FoodSuggestionEntity
import com.xcvi.micros.data.source.local.entity.message.MessageEntity
import com.xcvi.micros.data.source.local.entity.message.relations.MessageWithFoods
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao{

    @Query("SELECT COUNT(*) FROM MessageEntity")
    suspend fun messageCount(): Int

    @Transaction
    @Query("SELECT * FROM MessageEntity WHERE timestamp > :fromTimestamp ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun observeMessages(limit: Int, offset: Int, fromTimestamp: Long): Flow<List<MessageWithFoods>>

    @Query("SELECT * FROM FoodSuggestionEntity WHERE id = :id")
    suspend fun getSuggestion(id: String): FoodSuggestionEntity?

    @Query("SELECT * FROM MessageEntity WHERE timestamp = :timestamp AND fromUser = 0")
    suspend fun getMessage(timestamp: Long): MessageEntity?

    @Upsert
    suspend fun insertPartial(response: MessageEntity)

    @Upsert
    suspend fun insertPartial(suggestions: List<FoodSuggestionEntity>)

    @Transaction
    suspend fun insert(
        message: MessageEntity,
        suggestions: List<FoodSuggestionEntity>
    ) {
        insertPartial(message)
        insertPartial(suggestions)
    }

    @Query("DELETE FROM MessageEntity")
    suspend fun clear()

    @Query("DELETE FROM MessageEntity WHERE timestamp = :timestamp")
    suspend fun delete(timestamp: Long)
}
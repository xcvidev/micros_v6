package com.xcvi.micros.data.source.local.message

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.xcvi.micros.data.source.local.entity.message.FoodItemEntity
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

    @Query("SELECT * FROM FoodItemEntity WHERE id = :id")
    suspend fun getSuggestion(id: String): FoodItemEntity?

    @Query("SELECT * FROM MessageEntity WHERE timestamp = :timestamp AND fromUser = 0")
    suspend fun getMessage(timestamp: Long): MessageEntity?

    @Transaction
    @Query("SELECT * FROM MessageEntity WHERE timestamp = :timestamp")
    suspend fun getMessageWithFoods(timestamp: Long): MessageWithFoods?

    @Upsert
    suspend fun insertPartial(response: MessageEntity)

    @Upsert
    suspend fun insertPartial(suggestions: List<FoodItemEntity>)

    @Transaction
    suspend fun insert(
        message: MessageEntity,
        suggestions: List<FoodItemEntity>
    ) {
        insertPartial(message)
        insertPartial(suggestions)
    }

    @Query("DELETE FROM MessageEntity")
    suspend fun clear()

    @Query("DELETE FROM MessageEntity WHERE timestamp = :timestamp")
    suspend fun delete(timestamp: Long)
}
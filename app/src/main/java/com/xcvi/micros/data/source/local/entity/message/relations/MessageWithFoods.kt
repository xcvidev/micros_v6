package com.xcvi.micros.data.source.local.entity.message.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.xcvi.micros.data.source.local.entity.MessageEntity
import com.xcvi.micros.data.source.local.entity.FoodSuggestionEntity

data class MessageWithFoods(
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "timestamp",
        entityColumn = "messageTimestamp"
    )
    val suggestions: List<FoodSuggestionEntity>
)
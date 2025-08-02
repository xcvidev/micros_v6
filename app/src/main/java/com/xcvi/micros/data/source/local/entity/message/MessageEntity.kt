package com.xcvi.micros.data.source.local.entity.message

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey
    val timestamp: Long,
    val text: String,
    val fromUser: Boolean,
)

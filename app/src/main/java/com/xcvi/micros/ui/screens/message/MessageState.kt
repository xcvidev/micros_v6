package com.xcvi.micros.ui.screens.message

import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.utils.Failure

data class MessageState(
    val messages: List<Message> = emptyList(),
    val messageCount: Int = 0,
    val isLoadingMessage: Boolean = false,
    val isLoadingPage: Boolean = false,
    val error: Failure? = null,
    val endReached: Boolean = false,
)
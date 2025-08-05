package com.xcvi.micros.ui.screens.message

import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.message.Message
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.ui.screens.search.ScannerState

data class MessageState(
    val messages: List<Message> = emptyList(),
    val messageCount: Int = 0,
    val isLoadingMessage: Boolean = false,
    val isLoadingPage: Boolean = false,
    val error: Failure? = null,
    val endReached: Boolean = false,

    val scannerState: ScannerState = ScannerState.Scanning,
    val selected: Portion? = null,
    val isEnhancing: Boolean = false,
)
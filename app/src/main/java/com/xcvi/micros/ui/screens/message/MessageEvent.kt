package com.xcvi.micros.ui.screens.message

sealed interface MessageEvent{
    data object ShowHistory : MessageEvent
    data object ClearHistory : MessageEvent
    data class SendMessage(val userInput: String, val language: String, val onError: () -> Unit) :
        MessageEvent
    data object LoadMore : MessageEvent
}
package com.xcvi.micros.ui.screens.message

import com.xcvi.micros.domain.model.food.Portion

sealed interface MessageEvent{
    data object ShowHistory : MessageEvent
    data object ClearHistory : MessageEvent
    data class SendMessage(val userInput: String, val language: String, val onError: () -> Unit) :
        MessageEvent
    data object LoadMore : MessageEvent


    data class Scan(val barcode: String) : MessageEvent

    data object ResetScanner : MessageEvent

    data class OpenDetails(val portion: Portion) : MessageEvent
    data object CloseDetails : MessageEvent

    data object ToggleFavorite: MessageEvent
    data class Enhance(val input: String) : MessageEvent
    data class Scale(val amount: Int) : MessageEvent
    data class Confirm(val portion: Portion) : MessageEvent
}
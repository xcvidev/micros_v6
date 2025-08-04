package com.xcvi.micros.ui.screens.search

import com.xcvi.micros.domain.model.food.Portion

sealed interface SearchEvent {
    data class Input(val input: String) : SearchEvent
    data class Search(val date: Int, val meal: Int, val onError: () -> Unit) : SearchEvent

    data class OpenDetails(val portion: Portion) : SearchEvent
    data object CloseDetails : SearchEvent

    data object ToggleFavorite: SearchEvent
    data class Enhance(val input: String) : SearchEvent
    data class Scale(val amount: Int) : SearchEvent
    data class Select(val portion: Portion) : SearchEvent
    data class Confirm(val date: Int, val meal: Int,val onSuccess: () -> Unit) : SearchEvent

}
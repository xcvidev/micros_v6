package com.xcvi.micros.ui.screens.search

import androidx.compose.runtime.snapshots.SnapshotApplyResult
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.utils.Failure

sealed interface SearchEvent {
    data class Input(val input: String) : SearchEvent
    data class Search(val date: Int, val meal: Int, val onError: () -> Unit) : SearchEvent
    data class SmartSearch(val date: Int, val meal: Int, val onError: (Failure) -> Unit) : SearchEvent

    data class Scan(
        val date: Int,
        val meal: Int,
        val barcode: String
    ) : SearchEvent

    data object ResetScanner : SearchEvent

    data class OpenDetails(val portion: Portion) : SearchEvent
    data object CloseDetails : SearchEvent

    data object ToggleFavorite: SearchEvent
    data class Enhance(val input: String) : SearchEvent
    data class Scale(val amount: Int) : SearchEvent
    data class Select(val portion: Portion) : SearchEvent
    data class Confirm(val date: Int, val meal: Int,val onSuccess: () -> Unit) : SearchEvent

}
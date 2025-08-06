package com.xcvi.micros.ui.screens.search

import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.message.Message

data class SearchState(
    val scannerState: ScannerState = ScannerState.Scanning,
    val listLabel: String = "",
    val query: String = "",
    val searchResults: List<Portion> = emptyList(),
    val recents: List<Portion> = emptyList(),
    val selected: Portion? = null,
    val selectedItems: Set<Portion> = emptySet(),
    val isEnhancing: Boolean = false,
    val isSearching: Boolean = false,
    val isAsking: Boolean = false,
    val isLoadingSmartSearch: Boolean = false,
    val smartResult: Message? = null,
    )
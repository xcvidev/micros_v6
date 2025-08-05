package com.xcvi.micros.ui.screens.search

import com.xcvi.micros.domain.model.food.Portion

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
)
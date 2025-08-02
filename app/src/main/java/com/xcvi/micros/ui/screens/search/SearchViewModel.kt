package com.xcvi.micros.ui.screens.search

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.usecases.SearchUseCases
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SearchState(
    val isSearching: Boolean = false,
    val recents: List<Food> = emptyList(),
    val searchResults: List<Food> = emptyList(),
    val selectedFoodBarcodes: List<String> = emptyList()
)

class SearchViewModel(
    private val useCases: SearchUseCases,
) : BaseViewModel<SearchState>(SearchState())
package com.xcvi.micros.ui.screens.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.usecases.SearchUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

class SearchViewModel(
    context: Context,
    private val useCases: SearchUseCases,
) : BaseViewModel<SearchState>(SearchState()) {

    val language = context.getString(R.string.app_language)
    val recentsLabel = context.getString(R.string.recently_added)
    val searchLabel = context.getString(R.string.search_results)

    init {
        viewModelScope.launch {
            val recents = useCases.getRecents()
            updateData {
                copy(
                    searchResults = recents,
                    recents = recents,
                    listLabel = if (recents.isEmpty()) "" else recentsLabel
                )
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.Input -> setQuery(event.input)
            is SearchEvent.Search -> search(event.date, event.meal, event.onError)

            is SearchEvent.Select -> selectItem(event.portion)

            is SearchEvent.OpenDetails -> openDetails(event.portion)
            is SearchEvent.CloseDetails -> closeDetails()

            is SearchEvent.ToggleFavorite -> toggleFavorite()
            is SearchEvent.Enhance -> enhance(event.input)
            is SearchEvent.Scale -> scale(event.amount)

            is SearchEvent.Confirm -> onConfirm(
                date = event.date,
                meal = event.meal,
                onSuccess = event.onSuccess
            )
        }

    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val selected = state.selected ?: return@launch
            when(useCases.toggleFavorite(selected.food.barcode)){
                is Response.Success -> {
                    val updatedFood = selected.food.copy(isFavorite = !selected.food.isFavorite)
                    val updated = selected.copy(food = updatedFood)
                    updateData { copy(selected = updated) }
                }
                is Response.Error -> {}
            }
        }
    }

    private fun setQuery(query: String) {
        if (query == "") {
            updateData {
                copy(
                    searchResults = recents,
                    listLabel = if (recents.isEmpty()) "" else recentsLabel,
                    query = ""
                )
            }
            return
        }
        updateData { copy(query = query) }
    }

    private fun search(date: Int, meal: Int, onError: () -> Unit) {
        viewModelScope.launch {
            if (state.query.isBlank() || state.query.length < 3) return@launch
            updateData { copy(isSearching = true) }
            val res = useCases.search(
                query = state.query,
                language = language,
                date = date,
                meal = meal
            )
            when (res) {
                is Response.Success -> updateData {
                    copy(
                        searchResults = res.data,
                        listLabel = searchLabel
                    )
                }

                is Response.Error -> {
                    onError()
                    updateData { copy(searchResults = emptyList(), listLabel = "") }
                }
            }
            updateData { copy(isSearching = false) }
        }
    }

    private fun selectItem(portion: Portion) {
        updateData {
            val newChecked = selectedItems.toMutableSet()
            if (!newChecked.remove(portion)) {
                newChecked.add(portion)
            }
            copy(selectedItems = newChecked)
        }
    }

    private fun openDetails(portion: Portion) {
        updateData { copy(selected = portion) }
    }

    private fun closeDetails() {
        updateData { copy(selected = null) }
    }

    private fun enhance(input: String) {
        viewModelScope.launch {
            val current = state.selected ?: return@launch
            if (input.isBlank()) return@launch
            updateData { copy(isEnhancing = true) }
            val res = useCases.enhance(barcode = current.food.barcode, input)
            when (res) {
                is Response.Success -> {
                    val updated = res.data.scaleToPortion(
                        current.amount,
                        date = current.date,
                        meal = current.meal
                    )
                    updateHelper(updated)
                }

                is Response.Error -> {}
            }
            updateData { copy(isEnhancing = false) }
        }
    }

    private fun scale(amount: Int) {
        val current = state.selected ?: return
        val updated = current.scale(amount)
        updateHelper(updated)
    }

    private fun onConfirm(date: Int, meal: Int,onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = useCases.eat(
                date = date,
                meal = meal,
                state.selectedItems
            )
            when (res) {
                is Response.Success -> onSuccess()
                is Response.Error -> {}
            }
        }
    }

    private fun updateHelper(updated: Portion) {
        updateData {
            val updatedList =
                searchResults.map { if (it.food.barcode == updated.food.barcode) updated else it }
            val updatedSelected =
                if (selected?.food?.barcode == updated.food.barcode) updated else selected

            // Also update checkedItems if the item is checked
            val newChecked = selectedItems.toMutableSet()
            if (newChecked.removeIf { it.food.barcode == updated.food.barcode }) {
                newChecked.add(updated)
            }
            copy(
                searchResults = updatedList,
                selected = updatedSelected,
                selectedItems = newChecked
            )
        }
    }
}


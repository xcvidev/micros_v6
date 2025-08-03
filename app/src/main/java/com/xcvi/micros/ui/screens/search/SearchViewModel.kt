package com.xcvi.micros.ui.screens.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.usecases.SearchUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val searchResults: List<Portion> = emptyList(),
    val recents: List<Portion> = emptyList(),
    val selected: Portion? = null,
    val checkedItems: Set<Portion> = emptySet(),
    val isLoading: Boolean = false,
)

class SearchViewModel(
    context: Context,
    private val useCases: SearchUseCases,
) : BaseViewModel<SearchState>(SearchState()) {

    val language = context.getString(R.string.app_language)

    fun getRecents(date: Int, meal: Int) {
        viewModelScope.launch {
            useCases.getRecents().collect { list->
                val updatedList = list.map {
                    it.scaleToPortion(
                        date = date,
                        meal = meal,
                        portionAmount = 100
                    )
                }
                updateData { copy(recents = updatedList) }
            }
        }
    }

    fun setQuery(query: String) {
        if (query == "") {
            updateData { copy(searchResults = emptyList(), query = query) }
            return
        }
        updateData { copy(query = query) }
    }

    fun search(query: String, date: Int, meal: Int) {
        viewModelScope.launch {
            if (query.isBlank() || query.length < 3) return@launch
            val res = useCases.search(query, language)
            when (res) {
                is Response.Success -> {
                    val portions = res.data.map {
                        it.scaleToPortion(
                            date = date,
                            meal = meal,
                            portionAmount = 100
                        )
                    }
                    updateData { copy(searchResults = portions) }
                }

                is Response.Error -> updateData { copy(searchResults = emptyList()) }
            }
        }
    }

    fun onItemClicked(portion: Portion) {
        updateData { copy(selected = portion) }
    }

    fun onBottomSheetDismiss() {
        updateData { copy(selected = null) }
    }

    fun enhance(input: String) {
        viewModelScope.launch {
            val current = state.selected ?: return@launch
            if (input.isBlank()) return@launch
            updateData { copy(isLoading = true) }
            val res = useCases.enhance(barcode = current.food.barcode, input)
            when (res) {
                is Response.Success -> {
                    val updated = res.data.scaleToPortion(
                        current.amount,
                        date = current.date,
                        meal = current.meal
                    )
                    updateItem(updated)
                }

                is Response.Error -> {}
            }
            updateData { copy(isLoading = false) }
        }
    }

    fun scale(amount: Int) {
        val current = state.selected ?: return
        val updated = current.scale(amount)
        updateItem(updated)
    }


    fun toggleChecked(portion: Portion) {
        updateData {
            val newChecked = checkedItems.toMutableSet()
            if (!newChecked.remove(portion)) {
                newChecked.add(portion)
            }
            copy(checkedItems = newChecked)
        }
    }

    fun onConfirmChecked(onSuccess: () -> Unit) {
        viewModelScope.launch{
            val checkedFoods = state.checkedItems.toList()
            val res = useCases.eat(checkedFoods)
            when(res){
                is Response.Success -> onSuccess()
                is Response.Error -> {}
            }
        }
    }



    private fun updateItem(updated: Portion) {
        updateData {
            val updatedList =
                searchResults.map { if (it.food.barcode == updated.food.barcode) updated else it }
            val updatedSelected =
                if (selected?.food?.barcode == updated.food.barcode) updated else selected

            // Also update checkedItems if the item is checked
            val newChecked = checkedItems.toMutableSet()
            if (newChecked.removeIf { it.food.barcode == updated.food.barcode }) {
                newChecked.add(updated)
            }

            copy(
                searchResults = updatedList,
                selected = updatedSelected,
                checkedItems = newChecked
            )
        }
    }
}

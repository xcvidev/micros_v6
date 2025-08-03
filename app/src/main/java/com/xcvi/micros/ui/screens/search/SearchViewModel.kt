package com.xcvi.micros.ui.screens.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.usecases.SearchUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch


data class SearchState(
    val query: String = "",
    val recents: List<Food> = emptyList(),
    val searchResults: List<Food> = emptyList(),
    val selected: List<Portion> = emptyList()
)

class SearchViewModel(
    context: Context,
    private val useCases: SearchUseCases,
) : BaseViewModel<SearchState>(SearchState()) {

    val language = context.getString(R.string.app_language)

    fun eat(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (state.selected.isNotEmpty()) {
                when (useCases.eat(state.selected)) {
                    is Response.Error -> {}
                    is Response.Success -> onSuccess()
                }
            }
        }
    }

    fun cancelSelection() {
        updateData {
            copy(selected = emptyList())
        }
    }


    fun select(food: Food, date: Int, mealNumber: Int, amount: Int) {
        val portion = Portion(
            amount = amount,
            date = date,
            meal = mealNumber,
            food = food
        ).scale(amount)
        updateData {
            copy(selected = selected + portion)
        }
    }

    fun unselect(barcode: String) {
        val portion = state.selected.find { it.food.barcode == barcode } ?: return
        updateData {
            copy(selected = selected - portion)
        }
    }

    fun setQuery(query: String) {
        if (query == "") {
            updateData { copy(searchResults = emptyList()) }
        }
        updateData { copy(query = query) }
    }

    fun search() {
        viewModelScope.launch {
            if (state.query.length > 2 && state.query.isNotBlank()) {
                val res = useCases.search(state.query, language)
                when (res) {
                    is Response.Error -> {}
                    is Response.Success -> updateData { copy(searchResults = res.data) }
                }
            }
        }
    }

}
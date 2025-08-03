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
    val selected: List<Portion> = emptyList(),
    val isEnhancing: Boolean = false,
    val selectedPortion: Portion? = null,
)

class SearchViewModel(
    context: Context,
    private val useCases: SearchUseCases,
) : BaseViewModel<SearchState>(SearchState()) {

    val language = context.getString(R.string.app_language)

    fun showDetails(food: Food, date: Int, meal: Int) {
        val portion = state.selected.find {
            it.food.barcode == food.barcode
        } ?: Portion(
            food = food,
            amount = 100,
            date = date,
            meal = meal
        )
        updateData { copy(selectedPortion = portion) }
    }

    fun enhance(description: String) {
        viewModelScope.launch {
            val current = state.selectedPortion ?: return@launch
            updateData { copy(isEnhancing = true) }
            val res = useCases.enhance(current.food.barcode, description)
            when (res) {
                is Response.Error -> {}
                is Response.Success -> {
                    val portion = res.data.scaleToPortion(
                        current.amount,
                        date = current.date,
                        meal = current.meal
                    )
                    updateData { copy(selectedPortion = portion) }
                    replace(portion)

                }
            }
            updateData { copy(isEnhancing = false) }
        }
    }

    fun scale(newAmount: Int){
        val current = state.selectedPortion ?: return
        updateData {
            copy(selectedPortion = current.scale(newAmount))
        }
    }

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

    fun replace(portion: Portion) {
        updateData {
            val updated = selected.map {
                if (it.food.barcode == portion.food.barcode) portion else it
            }
            val updatedSearch = searchResults.map {
                if (it.barcode == portion.food.barcode) portion.food else it
            }
            copy(selected = updated, searchResults = updatedSearch)
        }
    }


    fun select(portion: Portion) {
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
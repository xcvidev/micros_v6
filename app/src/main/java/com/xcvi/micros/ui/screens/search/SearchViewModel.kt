package com.xcvi.micros.ui.screens.search

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.google.mlkit.vision.barcode.common.Barcode
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.usecases.SearchUseCases
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val recents: List<Food> = emptyList(),
    val searchResults: List<Food> = emptyList(),
    val selected: List<String> = emptyList()
)

class SearchViewModel(
    context: Context,
    private val useCases: SearchUseCases,
) : BaseViewModel<SearchState>(SearchState()) {

    val language = context.getString(R.string.app_language)

    fun add(barcode: String) {
        updateData {
            copy(selected = selected + barcode)
        }
    }

    fun setQuery(query: String) {
        if(query == ""){
            updateData { copy(searchResults = emptyList()) }
        }
        updateData { copy(query = query) }
    }

    fun saveSelected(date: Int, mealNumber: Int){
        viewModelScope.launch {
            if(state.selected.isNotEmpty()){
                useCases.eat(date,mealNumber,state.selected)
            }
        }
    }

    fun search() {
        viewModelScope.launch {
            if(state.query.length > 3 && state.query.isNotBlank()){
                val res = useCases.search(state.query, language)
                when (res) {
                    is Response.Error -> {}
                    is Response.Success -> updateData { copy(searchResults = res.data) }
                }
            }
        }
    }

}
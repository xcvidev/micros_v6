package com.xcvi.micros.ui.screens.details

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.Food
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.usecases.DetailsUseCases
import com.xcvi.micros.domain.utils.Failure
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

data class DetailsState(
    val portion: Portion? = null,
    val isLoading: Boolean = false,
    val description: String = "",
)

class DetailsViewModel(
    applicationContext: Context,
    private val useCases: DetailsUseCases,
) : BaseViewModel<DetailsState>(DetailsState()) {

    fun enhance(barcode: String, description: String) {
        viewModelScope.launch {
            updateData { copy(isLoading = true) }
            val current = state.portion ?: return@launch
            val res = useCases.enhance(barcode, description)
            when (res) {
                is Response.Error -> {}
                is Response.Success -> {
                    val portion = res.data.scaleToPortion(
                        current.amount,
                        date = current.date,
                        meal = current.meal
                    )
                    updateData { copy(portion = portion) }
                }
            }
            updateData { copy(isLoading = false) }
        }
    }

    fun scale(amount: Int) {
        updateData { copy(portion = portion?.scale(amount)) }
    }

    fun setPortion(portion: Portion) {
        updateData { copy(portion = portion) }
    }

    fun clear(){
        updateData { copy(portion = null) }
    }
}
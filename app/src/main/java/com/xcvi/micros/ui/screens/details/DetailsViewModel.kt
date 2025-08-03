package com.xcvi.micros.ui.screens.details

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.usecases.DetailsUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

data class DetailsState(
    val portion: Portion? = null,
    val amount: Int = 0,
    val isLoading: Boolean = false,
    val description: String = "",
)

class DetailsViewModel(
    applicationContext: Context,
    private val useCases: DetailsUseCases,
) : BaseViewModel<DetailsState>(DetailsState()) {

    fun enhance() {
        viewModelScope.launch {
            val current = state.portion ?: return@launch
            when (val result =
                useCases.enhance(current.food.barcode, description = state.description)) {
                is Response.Error -> {}
                is Response.Success -> updateData {
                    copy(
                        portion = result.data.scaleToPortion(
                            portionAmount = amount,
                            date = current.date,
                            current.meal
                        )
                    )
                }
            }
        }
    }

    fun scale(amount: Int) {
        updateData { copy(portion = portion?.scale(amount)) }
    }
}
package com.xcvi.micros.ui.screens.scan

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.usecases.ScanUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

sealed class ScanState {
    data class Success(
        val portion: Portion,
        val isEnhancing: Boolean = false,
    ) : ScanState()
    object Camera : ScanState()
    object Loading : ScanState()
    object Error : ScanState()
}
sealed interface ScanEvent {
    data class Scan(val date: Int, val meal: Int, val barcode: String) : ScanEvent
    data class Confirm(val onSuccess: () -> Unit) : ScanEvent
    data class Enhance(val input: String) : ScanEvent
    data object ToggleFavorite : ScanEvent
    data class Scale(val amount: Int) : ScanEvent

}

class ScanViewModel(
    private val useCases: ScanUseCases
) : BaseViewModel<ScanState>(ScanState.Camera) {

    fun onEvent(event: ScanEvent) {
        when (event) {
            is ScanEvent.Scan -> onScan(event.date, event.meal, event.barcode)
            is ScanEvent.Confirm -> savePortion(event.onSuccess)
            is ScanEvent.Enhance -> enhance(event.input)
            is ScanEvent.Scale -> scale(event.amount)
            is ScanEvent.ToggleFavorite -> toggleFavorite()
        }
    }


    private fun toggleFavorite() {
        viewModelScope.launch {
            when(state){
                is ScanState.Success -> {
                    val selected = (state as ScanState.Success).portion
                    when(useCases.toggleFavorite(selected.food.barcode)){
                        is Response.Success -> {
                            val updatedFood = selected.food.copy(isFavorite = !selected.food.isFavorite)
                            val updated = selected.copy(food = updatedFood)
                            state = ScanState.Success(updated)
                        }
                        is Response.Error -> {}
                    }
                }
                else -> return@launch
            }
        }
    }

    private fun savePortion(onSaved: () -> Unit) {
        viewModelScope.launch {
            if (state is ScanState.Success){
                val portion = (state as ScanState.Success).portion
                when (
                    useCases.updatePortion(
                        newAmount = portion.amount,
                        date = portion.date,
                        meal = portion.meal,
                        barcode = portion.food.barcode
                    )
                ) {
                    is Response.Success -> onSaved()
                    is Response.Error -> {}
                }

            }
        }
    }

    private fun scale(amount: Int) {
        if (state !is ScanState.Success) {
            return
        } else{
            val current = (state as ScanState.Success).portion
            val updated = current.scale(amount)
            state = ScanState.Success(updated)
        }
    }


    private fun enhance(input: String) {
        viewModelScope.launch {
            if (state !is ScanState.Success) {
                return@launch
            } else {
                val current = (state as ScanState.Success).portion
                if (input.isBlank()) return@launch
                state = (state as ScanState.Success).copy(isEnhancing = true)
                val res = useCases.enhance(barcode = current.food.barcode, input)
                when (res) {
                    is Response.Success -> {
                        val updated = res.data.scaleToPortion(
                            current.amount,
                            date = current.date,
                            meal = current.meal
                        )
                        state = ScanState.Success(updated)
                    }

                    is Response.Error -> {}
                }
            }
        }
    }

    private fun onScan(
        date: Int,
        meal: Int,
        barcode: String
    ) {
        viewModelScope.launch {
            state = ScanState.Loading
            when (val res = useCases.scan(barcode)) {
                is Response.Success -> {
                    val portion = Portion(
                        date = date,
                        meal = meal,
                        amount = 100,
                        food = res.data
                    )
                    state = ScanState.Success(portion)
                }

                is Response.Error -> state = ScanState.Error
            }

        }
    }
}
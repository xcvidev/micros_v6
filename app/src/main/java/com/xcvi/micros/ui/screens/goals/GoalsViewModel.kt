package com.xcvi.micros.ui.screens.goals

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.usecases.GoalsUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class GoalsViewModel(
    private val useCases: GoalsUseCases,
) : BaseViewModel<GoalsState>(GoalsState.Loading) {

    init {
        viewModelScope.launch {
            getSummary(getToday())
        }
    }


    fun onEvent(event: GoalsEvent) {
        when (event) {
            is GoalsEvent.SetCurrentGoals -> update(event.protein, event.carbs, event.fats, event.onError)
        }
    }

    private var summaryJob: Job? = null
    private fun getSummary(date: Int) {
        summaryJob?.cancel()
        summaryJob = viewModelScope.launch {
            useCases.getSummary(date).collect{ data ->
                if(data.hasGoals()){
                    state = GoalsState.Goals(currentGoals = data)
                } else {
                    state = GoalsState.Empty
                }
            }
        }
    }

    private fun update(protein: Int, carbs: Int, fats: Int, onError: () -> Unit){
        viewModelScope.launch {
            state = GoalsState.Loading
            when (useCases.updateMacroGoals(protein, carbs, fats)) {
                is Response.Error -> {}
                is Response.Success -> {
                    getSummary(getToday())
                }
            }

        }
    }

}


sealed class GoalsState{
    data object Loading : GoalsState()
    data object Empty: GoalsState()
    data class Goals(val currentGoals: MacrosSummary) : GoalsState()
}

sealed interface GoalsEvent {
    data class SetCurrentGoals(val protein: Int, val carbs: Int, val fats: Int, val onError: () -> Unit) : GoalsEvent
}
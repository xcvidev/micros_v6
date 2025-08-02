package com.xcvi.micros.ui.screens.goals

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.usecases.GoalsUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch



class GoalsViewModel(
    private val useCases: GoalsUseCases,
) : BaseViewModel<GoalsState>(GoalsState()) {

    init {
        getData(getToday())
    }


    fun onEvent(event: GoalsEvent) {
        when (event) {
            is GoalsEvent.SetCurrentGoals -> update(event.protein, event.carbs, event.fats)
        }
    }

    private fun getData(date: Int) {
        viewModelScope.launch {
            updateData { copy(isLoading = true) }
            useCases.getGoal(date).collect{ data ->
                updateData {
                    copy(
                        isLoading = false,
                        currentGoals = data
                    )
                }

            }

        }
    }

    private fun update(protein: Int, carbs: Int, fats: Int){
        viewModelScope.launch {
            updateData { copy(isLoading = true) }
            when (
                val res = useCases.updateMacroGoals(protein, carbs, fats)
            ) {
                is Response.Error -> updateData { copy(isLoading = false) }
                is Response.Success -> TODO()
            }

        }
    }

}


data class GoalsState(
    val isLoading: Boolean = false,
    val currentGoals: MacrosSummary? = null,
)

sealed interface GoalsEvent {
    data class SetCurrentGoals(val protein: Int, val carbs: Int, val fats: Int) : GoalsEvent
}
package com.xcvi.micros.ui.screens.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.xcvi.micros.R
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.MacrosSummary
import com.xcvi.micros.domain.model.food.Meal
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.food.sumAminoAcids
import com.xcvi.micros.domain.model.food.sumMinerals
import com.xcvi.micros.domain.model.food.sumNutrients
import com.xcvi.micros.domain.model.food.sumVitamins
import com.xcvi.micros.domain.usecases.DashboardUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface DashboardEvent {
    data class ChangeDate(val date: Int) : DashboardEvent
    data object AddMeal : DashboardEvent
    data class PasteMeal(val source: Meal?, val destination: Meal, val onError: () -> Unit, val onSuccess: () -> Unit) :
        DashboardEvent
    data class DeleteMeal(val selectedMeal: Meal,val onError: () -> Unit) : DashboardEvent
}

data class DashboardState(
    val currentDate: Int = getToday(),
    val meals: List<Meal> = emptyList(),
    val visibleMeals: Set<Int> = emptySet(),
    val summary: MacrosSummary = MacrosSummary.empty(),

    val nutrients: Nutrients = Nutrients.empty(),
    val minerals: Minerals = Minerals.empty(),
    val vitamins: Vitamins = Vitamins.empty(),
    val aminoAcids: AminoAcids = AminoAcids.empty(),
)

class DashboardViewModel(
    applicationContext: Context,
    private val useCases: DashboardUseCases,
) : BaseViewModel<DashboardState>(DashboardState()) {

    val mealNames = mapOf(
        1 to applicationContext.getString(R.string.meal1),
        2 to applicationContext.getString(R.string.meal2),
        3 to applicationContext.getString(R.string.meal3),
        4 to applicationContext.getString(R.string.meal4),
        5 to applicationContext.getString(R.string.meal5),
        6 to applicationContext.getString(R.string.meal6),
        7 to applicationContext.getString(R.string.meal7),
        8 to applicationContext.getString(R.string.meal8)
    )

    init {
        val today = getToday()
        observeMeals(today)
        observeGoal(today)
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.ChangeDate -> changeDateDate(event.date)
            is DashboardEvent.PasteMeal -> pasteMeal(
                event.source,
                event.destination,
                event.onError,
                event.onSuccess
            )

            is DashboardEvent.AddMeal -> addMeal()
            is DashboardEvent.DeleteMeal -> deleteMeal(event.selectedMeal, event.onError)
        }
    }

    private var mealObserverJob: Job? = null
    private fun observeMeals(date: Int) {
        mealObserverJob?.cancel()
        mealObserverJob = viewModelScope.launch {
            useCases.observeMeals(date, mealNames).collect { meals ->
                val updatedMeals = meals.map { meal ->
                    val hasContent = meal.portions.isNotEmpty()
                    val isVisible = hasContent || state.visibleMeals.contains(meal.number)
                    meal.copy(isVisible = isVisible)
                }

                val portions = updatedMeals.flatMap { it.portions }
                val nutrients = portions.sumNutrients()
                val minerals = portions.sumMinerals()
                val vitamins = portions.sumVitamins()
                val aminoAcids = portions.sumAminoAcids()
                updateData {
                    copy(
                        meals = meals,
                        nutrients = nutrients,
                        minerals = minerals,
                        vitamins = vitamins,
                        aminoAcids = aminoAcids
                    )
                }

                if (updatedMeals.none { it.isVisible || it.isPinned }){
                    addMeal()
                }
            }
        }
    }
    private fun addMeal() {
        val index = state.meals.indexOfFirst { !it.isVisible }
        if (index == -1 || index >= 8) return

        val mealId = state.meals[index].number


        updateData {
            copy(
                visibleMeals = visibleMeals + mealId,
                meals = meals.toMutableList().also {
                    it[index] = it[index].copy(isVisible = true)
                }
            )
        }
    }

    private fun changeDateDate(date: Int) {
        updateData { copy(currentDate = date, visibleMeals = emptySet()) }
        observeMeals(date)
        observeGoal(date)
    }


    private fun pasteMeal(
        source: Meal?,
        destination: Meal,
        onError: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            if (source == null) {
                onError()
                return@launch
            }
            when (
                useCases.pasteMeal(
                    meal = source,
                    newDate = destination.date,
                    newNumber = destination.number
                )
            ) {
                is Response.Error -> onError()
                is Response.Success -> onSuccess()
            }
        }
    }

    private fun deleteMeal(selectedMeal: Meal, onError: () -> Unit) {
        fun hideMeal(meal: Meal) {
            val index = state.meals.indexOfFirst { it.number == meal.number }
            if (index == -1) return
            if (meal.number == 1) return

            updateData {
                copy(
                    visibleMeals = visibleMeals - meal.number,
                    meals = meals.toMutableList().also {
                        it[index] = it[index].copy(isVisible = false)
                    }
                )
            }
        }

        viewModelScope.launch {
            when (useCases.deleteMeal(selectedMeal)) {
                is Response.Error -> onError()
                is Response.Success -> hideMeal(selectedMeal)

            }
        }
    }




    private var goalObserverJob: Job? = null
    private fun observeGoal(date: Int) {
        goalObserverJob?.cancel()
        goalObserverJob = viewModelScope.launch {
            useCases.observeGoal(date).collect { g ->
                updateData { copy(summary = g) }
            }
        }
    }
}
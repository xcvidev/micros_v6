package com.xcvi.micros.ui.screens.meal

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.model.food.sumAminos
import com.xcvi.micros.domain.model.food.sumMinerals
import com.xcvi.micros.domain.model.food.sumNutrients
import com.xcvi.micros.domain.model.food.sumVitamins
import com.xcvi.micros.domain.usecases.MealUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

data class MealState(
    val isEnhancing: Boolean = false,
    val portions: List<Portion> = emptyList(),
    val selected: Portion? = null,
    val nutrients: Nutrients = Nutrients.empty(),
    val minerals: Minerals = Minerals.empty(),
    val vitamins: Vitamins = Vitamins.empty(),
    val aminoAcids: AminoAcids = AminoAcids.empty()
)

sealed interface MealEvent {
    data class GetMeal(val date: Int, val number: Int) : MealEvent

    data object Confirm : MealEvent
    data class Enhance(val input: String) : MealEvent
    data object ToggleFavorite : MealEvent
    data class Scale(val amount: Int) : MealEvent

    data class Clear(val date: Int, val meal: Int) : MealEvent
    data object DeletePortion : MealEvent
    data class OpenDetails(val portion: Portion) : MealEvent
    data object CloseDetails : MealEvent

}

class MealViewModel(
    private val useCases: MealUseCases
) : BaseViewModel<MealState>(MealState()) {

    fun onEvent(event: MealEvent) {
        when (event) {
            is MealEvent.Clear -> clearMeal(date = event.date, meal = event.meal)
            is MealEvent.GetMeal -> observeMeal(date = event.date, event.number)

            is MealEvent.OpenDetails -> openDetails(event.portion)
            is MealEvent.CloseDetails -> closeDetails()

            is MealEvent.Confirm -> savePortion()
            is MealEvent.Enhance -> enhance(event.input)
            is MealEvent.ToggleFavorite -> toggleFavorite()
            is MealEvent.Scale -> scale(event.amount)
            is MealEvent.DeletePortion -> deletePortion()
        }
    }

    private fun clearMeal(date: Int, meal: Int) {
        viewModelScope.launch {
            useCases.clearMeal(date = date, meal = meal)
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val selected = state.selected ?: return@launch
            when(useCases.toggleFavorite(selected.food.barcode)){
                is Response.Success -> {
                    val updatedFood = selected.food.copy(isFavorite = !selected.food.isFavorite)
                    val updated = selected.copy(food = updatedFood)
                    updateData { copy(selected = updated) }
                }
                is Response.Error -> {}
            }
        }
    }

    private fun savePortion() {
        viewModelScope.launch {
            val portion = state.selected ?: return@launch
            when (
                useCases.updatePortion(
                    newAmount = portion.amount,
                    date = portion.date,
                    meal = portion.meal,
                    barcode = portion.food.barcode
                )
            ) {
                is Response.Success -> closeDetails()
                is Response.Error -> {}
            }
        }
    }

    private fun scale(amount: Int) {
        val current = state.selected ?: return
        val updated = current.scale(amount)
        updateData { copy(selected = updated) }
    }


    private fun enhance(input: String) {
        viewModelScope.launch {
            val current = state.selected ?: return@launch
            if (input.isBlank()) return@launch
            updateData { copy(isEnhancing = true) }
            val res = useCases.enhance(barcode = current.food.barcode, input)
            when (res) {
                is Response.Success -> {
                    val updated = res.data.scaleToPortion(
                        current.amount,
                        date = current.date,
                        meal = current.meal
                    )
                    updateData { copy(selected = updated) }
                }

                is Response.Error -> {}
            }
            updateData { copy(isEnhancing = false) }
        }
    }

    private fun openDetails(portion: Portion) {
        updateData { copy(selected = portion) }
    }

    private fun closeDetails() {
        updateData { copy(selected = null) }
    }

    private fun savePortion(portion: Portion, amount: Int) {
        viewModelScope.launch {
            useCases.updatePortion(
                newAmount = amount,
                date = portion.date,
                meal = portion.meal,
                barcode = portion.food.barcode
            )
        }
    }

    private fun observeMeal(date: Int, number: Int) {
        viewModelScope.launch {
            useCases.getMealData(date = date, number).collect {
                val minerals = it.sumMinerals()
                val vitamins = it.sumVitamins()
                val nutrients = it.sumNutrients()
                val aminoAcids = it.sumAminos()
                updateData {
                    copy(
                        portions = it,
                        minerals = minerals,
                        vitamins = vitamins,
                        nutrients = nutrients,
                        aminoAcids = aminoAcids
                    )
                }
            }
        }
    }


    private fun deletePortion() {
        viewModelScope.launch {
            val portion = state.selected ?: return@launch
            when (
                useCases.deletePortion(
                    date = portion.date,
                    meal = portion.meal,
                    barcode = portion.food.barcode
                )
            ) {
                is Response.Success -> closeDetails()
                is Response.Error -> {}
            }
        }
    }
}
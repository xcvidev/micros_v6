package com.xcvi.micros.ui.screens.meal

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.AminoAcids
import com.xcvi.micros.domain.model.food.Minerals
import com.xcvi.micros.domain.model.food.Nutrients
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.Vitamins
import com.xcvi.micros.domain.model.food.sumAminos
import com.xcvi.micros.domain.model.food.sumMinerals
import com.xcvi.micros.domain.model.food.sumNutrients
import com.xcvi.micros.domain.model.food.sumVitamins
import com.xcvi.micros.domain.usecases.MealUseCases
import com.xcvi.micros.domain.utils.nextAmount
import com.xcvi.micros.domain.utils.previousAmount
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

data class MealState(
    val portions: List<Portion> = emptyList(),
    val deletePortion: Portion? = null,
    val nutrients: Nutrients = Nutrients.empty(),
    val minerals: Minerals = Minerals.empty(),
    val vitamins: Vitamins = Vitamins.empty(),
    val aminoAcids: AminoAcids = AminoAcids.empty()
)

sealed interface MealEvent {
    data class GetMeal(val date: Int, val number: Int): MealEvent

    data class UpdatePortion(
        val portion: Portion,
        val isIncrement: Boolean,
        val onDelete: () -> Unit
    ) : MealEvent

    data class SavePortion(
        val portion: Portion,
        val amount: Int,
    ) : MealEvent

    data object DeletePortion : MealEvent
    data class SelectPortion(val portion: Portion) : MealEvent
}

class MealViewModel(
    private val useCases: MealUseCases
) : BaseViewModel<MealState>(MealState()) {

    fun onEvent(event: MealEvent) {
        when (event) {
            is MealEvent.GetMeal -> observeMeal(date = event.date, event.number)

            is MealEvent.DeletePortion -> deletePortion()

            is MealEvent.SelectPortion -> updateData { copy(deletePortion = event.portion) }
            is MealEvent.UpdatePortion -> updatePortion(
                event.portion,
                event.isIncrement,
                event.onDelete
            )

            is MealEvent.SavePortion -> savePortion(event.portion, event.amount)
        }
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

    private fun observeMeal(date: Int, number: Int){
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


    private fun updatePortion(portion: Portion, isIncrement: Boolean, onDelete: () -> Unit) {
        viewModelScope.launch {
            val newAmount = if (isIncrement) {
                portion.amount.nextAmount()
            } else {
                portion.amount.previousAmount()
            }

            if (newAmount <= 0) {
                onDelete()
                updateData { copy(deletePortion = portion) }
                return@launch
            }

            useCases.updatePortion(
                newAmount = newAmount,
                date = portion.date,
                meal = portion.meal,
                barcode = portion.food.barcode
            )
        }
    }

    private fun deletePortion() {
        viewModelScope.launch {
            val portion = state.deletePortion ?: return@launch

            useCases.deletePortion(
                date = portion.date,
                meal = portion.meal,
                barcode = portion.food.barcode
            )
            updateData { copy(deletePortion = null) }
        }
    }
}
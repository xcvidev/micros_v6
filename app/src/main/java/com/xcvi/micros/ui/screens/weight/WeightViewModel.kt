package com.xcvi.micros.ui.screens.weight

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.weight.Weight
import com.xcvi.micros.domain.model.weight.WeightUnit
import com.xcvi.micros.domain.usecases.WeightUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeightViewModel(
    private val useCases: WeightUseCases
) : BaseViewModel<WeightState>(WeightState()) {

    init {
        getLatest()
        observeData(state.currentDate)
    }

    fun onEvent(event: WeightEvent) {
        when (event) {
            is WeightEvent.SetDate -> setDate(event.date)
            is WeightEvent.Save -> save(event.onError)
            is WeightEvent.SetPickerValue -> updateData { copy(pickerValue = event.value) }
            is WeightEvent.SetDeleteWeight -> updateData { copy(deleteWeight = event.weight) }
            is WeightEvent.ConfirmDelete -> delete()
            is WeightEvent.ToggleUnit -> toggleUnit()
        }
    }

    private fun toggleUnit() {
        viewModelScope.launch {
            val newUnit = if (state.unit == WeightUnit.kg) WeightUnit.lbs else WeightUnit.kg
            when (useCases.changeUnit(newUnit)) {
                is Response.Error -> {}
                is Response.Success -> {
                    updateData { copy(unit = newUnit, initialValue = null) }
                    delay(50)
                    val newInitialValue = if (newUnit == WeightUnit.kg) {
                        state.pickerValue / 2.2046
                    } else {
                        state.pickerValue * 2.2046
                    }


                    updateData { copy(unit = newUnit, initialValue = newInitialValue) }
                    observeData(state.currentDate)
                }
            }
        }
    }

    private var observeJob: Job? = null
    private fun observeData(date: Int) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            useCases.getData(date).collect {
                updateData {
                    copy(weights = it)
                }
            }
        }
    }

    private fun setDate(date: Int) {
        updateData {
            copy(currentDate = date)
        }
        observeData(date)
    }

    private fun getLatest() {
        viewModelScope.launch {
            val defaultWeight = Weight.Companion.empty.copy(weight = 80.0)
            val latest = when (val response = useCases.getLatest()) {
                is Response.Error -> defaultWeight
                is Response.Success -> response.data
            }
            updateData {
                copy(
                    initialValue = if (latest.weight == 0.0) defaultWeight.weight else latest.weight,
                    unit = latest.unit
                )
            }
        }
    }

    private fun save(onError: () -> Unit = {}) {
        if (state.pickerValue <= 1.0) return
        viewModelScope.launch {
            //updateData { copy(initialValue = null) }
            delay(50)
            when (useCases.save(state.pickerValue, state.currentDate, state.unit)) {
                is Response.Error -> onError()
                is Response.Success -> {}
            }
            //updateData { copy(initialValue = state.pickerValue) }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            val w = state.deleteWeight ?: return@launch
            useCases.delete(w)
        }
        updateData {
            copy(deleteWeight = null)
        }

    }


}
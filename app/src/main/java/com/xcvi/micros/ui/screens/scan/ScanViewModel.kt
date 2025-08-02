package com.xcvi.micros.ui.screens.scan

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.usecases.ScanUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

class ScanViewModel(
    private val useCases: ScanUseCases
) : BaseViewModel<ScanViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false,
    )

    fun cacheScan(barcode: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            updateData { copy(isLoading = true) }
            when (useCases.scan(barcode)) {
                is Response.Success -> {
                    onSuccess()
                }

                is Response.Error -> {
                    onFailure()
                }
            }

        }
    }
}
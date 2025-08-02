package com.xcvi.micros.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T>(initial: T) : ViewModel() {
    var state by mutableStateOf(initial)
        protected set
    protected fun updateData(update: T.() -> T) {
        state = state.update()
    }
}

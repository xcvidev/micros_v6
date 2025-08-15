package com.xcvi.micros.ui.screens.message

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.model.food.Portion
import com.xcvi.micros.domain.model.food.scale
import com.xcvi.micros.domain.model.food.scaleToPortion
import com.xcvi.micros.domain.usecases.MessageUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getNow
import com.xcvi.micros.domain.utils.getToday
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.screens.search.ScannerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageViewModel(
    private val useCases: MessageUseCases
) : BaseViewModel<MessageState>(MessageState()) {

    private var startingTimestamp = getNow()
    private var messagesJob : Job? = null
    private var offset = 0
    private val limit = 10


    init {
        getCount()
        //observeMessages(isPaginating = false)
    }

    fun onEvent(event: MessageEvent){
        when(event){

            is MessageEvent.ShowHistory -> showHistory()
            is MessageEvent.ClearHistory -> clearHistory()
            is MessageEvent.SendMessage -> sendMessage(event.userInput, event.language, event.onError)
            is MessageEvent.LoadMore -> onLoadMore()

            is MessageEvent.OpenDetails -> {
                openDetails(event.portion)
                updateData { copy(scannerState = ScannerState.ShowResult) }
            }
            is MessageEvent.CloseDetails -> {
                closeDetails()
                updateData { copy(scannerState = ScannerState.Scanning) }
            }

            is MessageEvent.Scan -> scan(barcode = event.barcode){ openDetails(it) }
            is MessageEvent.ResetScanner -> updateData { copy(scannerState = ScannerState.Scanning) }

            is MessageEvent.ToggleFavorite -> toggleFavorite()
            is MessageEvent.Enhance -> enhance(event.input)
            is MessageEvent.Scale -> scale(event.amount)

            is MessageEvent.Confirm -> confirm(event.portion)
            is MessageEvent.GetData -> {
                val timestamp =  getNow()
                observeMessages(isPaginating = false, timestamp =timestamp)
            }
        }
    }

    private fun openDetails(portion: Portion) {
        updateData { copy(selected = portion) }
    }

    private fun closeDetails() {
        updateData { copy(selected = null) }
    }

    private fun scan(barcode: String, onSuccess: (Portion) -> Unit) {
        viewModelScope.launch {
            updateData { copy(scannerState = ScannerState.Loading) }
            delay(500)
            val res = useCases.scan(barcode = barcode, date = getToday(), meal = 1)
            when (res) {
                is Response.Error -> updateData { copy(scannerState = ScannerState.Error) }
                is Response.Success -> {
                    updateData { copy(scannerState = ScannerState.ShowResult) }
                    onSuccess(res.data)
                }
            }
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

    private fun confirm(portion: Portion) {
        viewModelScope.launch {
            when (
                useCases.eat(portion)
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



    private fun onLoadMore(){
        if (state.endReached) return
        offset += limit
        observeMessages(isPaginating = true)
    }

    private fun showHistory() {
        offset = 0
        startingTimestamp = 0
        updateData { copy(endReached = false) }
        observeMessages(isPaginating = false)
    }

    private fun clearHistory() {
        viewModelScope.launch {
            when(useCases.clearHistory()){
                is Response.Error -> {}
                is Response.Success -> {
                    updateData { copy(messageCount = 0) }
                }
            }
        }
    }

    private fun sendMessage(userInput: String, language: String, onError: () -> Unit) {
        if (userInput.isBlank()) {
            onError()
            return
        }
        viewModelScope.launch {
            val recentMessages = state.messages.takeLast(4).sortedBy { it.timestamp } // very important to sort.

            updateData { copy(isLoadingMessage = true) }

            when (val result = useCases.askAssistant(
                userInput = userInput,
                language = language,
                recentMessages = recentMessages
            )) {
                is Response.Error -> {
                    onError()
                    updateData { copy(isLoadingMessage = false, error = result.error) }
                }

                is Response.Success -> {
                    updateData {
                        copy(
                            isLoadingMessage = false,
                            error = null,
                            messageCount = messageCount + 1,
                        )
                    }
                }
            }
        }
    }

    private fun getCount(){
        viewModelScope.launch {
            val count = useCases.getMessageCount()
            updateData { copy(messageCount = count) }
        }
    }

    private fun observeMessages(isPaginating: Boolean, timestamp: Long = startingTimestamp){
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            updateData { copy(isLoadingPage = true) }
            useCases.observeMessages(limit = limit, offset = offset, fromTimestamp = timestamp)
                .collect { newMessages ->
                    updateData {
                        val updatedMessages = if (isPaginating) messages + newMessages else newMessages
                        copy(
                            messages = updatedMessages,
                            isLoadingPage = false,
                            endReached = newMessages.size < limit
                        )
                    }
                }
        }
    }

}

package com.xcvi.micros.ui.screens.message

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.usecases.MessageUseCases
import com.xcvi.micros.domain.utils.Response
import com.xcvi.micros.domain.utils.getNow
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Job
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
        observeMessages(isPaginating = false)
    }

    fun onEvent(event: MessageEvent){
        when(event){
            is MessageEvent.ShowHistory -> showHistory()
            is MessageEvent.ClearHistory -> clearHistory()
            is MessageEvent.SendMessage -> sendMessage(event.userInput, event.language, event.onError)
            is MessageEvent.LoadMore -> onLoadMore()
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

    private fun observeMessages(isPaginating: Boolean){
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            updateData { copy(isLoadingPage = true) }
            useCases.observeMessages(limit = limit, offset = offset, fromTimestamp = startingTimestamp)
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

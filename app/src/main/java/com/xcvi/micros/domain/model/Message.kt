package com.xcvi.micros.domain.model

data class Message(
    val timestamp: Long,
    val text: String?,
    val fromUser: Boolean,
    val suggestions: List<Suggestion> = emptyList()
)
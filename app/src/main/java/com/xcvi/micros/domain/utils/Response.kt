package com.xcvi.micros.domain.utils

sealed interface Response<out D> {
    data class Success<out D>(val data: D) : Response<D>
    data class Error(val error: Failure) : Response<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error
}


sealed class Failure: Throwable() {
    data object Network: Failure() {
        private fun readResolve(): Any = Network
    }
    data object EmptyResult: Failure() {
        private fun readResolve(): Any = message ?: "Empty result"
    }
    data object InvalidInput: Failure() {
        private fun readResolve(): Any = InvalidInput
    }
    data object Unknown: Failure() {
        private fun readResolve(): Any = Unknown
    }
    data object Database: Failure() {
        private fun readResolve(): Any = Database
    }
    data object Authentication: Failure() {
        private fun readResolve(): Any = Authentication
    }
    data object AlreadyExists: Failure() {
        private fun readResolve(): Any = AlreadyExists
    }
}
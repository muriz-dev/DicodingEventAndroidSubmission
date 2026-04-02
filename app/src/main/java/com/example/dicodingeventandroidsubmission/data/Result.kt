package com.example.dicodingeventandroidsubmission.data

sealed class Result<out R> {
    data class Success<out R>(val value: R) : Result<R>()
    data class Error(val error: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

fun <T> Result<T>.handle(
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit,
    onSuccess: (T) -> Unit
) {
    when (this) {
        is Result.Loading -> {
            onLoading(true)
        }
        is Result.Success -> {
            onLoading(false)
            onSuccess(this.value)
        }
        is Result.Error -> {
            onLoading(false)
            onError(this.error)
        }
    }
}
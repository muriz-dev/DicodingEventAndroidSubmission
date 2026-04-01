package com.example.dicodingeventandroidsubmission.data

sealed class Result<out R> {
    data class Success<out R>(val value: R) : Result<R>()
    data class Error(val error: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
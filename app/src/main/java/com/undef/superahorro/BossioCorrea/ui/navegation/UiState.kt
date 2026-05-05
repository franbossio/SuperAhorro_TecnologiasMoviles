package com.undef.superahorro.BossioCorrea.ui.navigation

sealed class UiState<out T> {
    object Loading                     : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val msg: String = "Error desconocido") : UiState<Nothing>()
}
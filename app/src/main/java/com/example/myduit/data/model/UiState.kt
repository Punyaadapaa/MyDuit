package com.example.myduit.data.model

/**
 * Generic sealed interface untuk merepresentasikan state UI.
 * Bisa dipakai ulang di ViewModel manapun yang butuh pola Idle/Loading/Success/Error.
 */
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

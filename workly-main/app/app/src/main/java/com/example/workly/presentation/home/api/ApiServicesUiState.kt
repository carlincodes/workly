package com.example.workly.presentation.api

data class ApiServicesUiState(
    val isLoading: Boolean = false,
    val cepInput: String = "",
    val addressResult: String? = null,
    val errorMessage: String? = null
) {
    val hasResult: Boolean get() = !isLoading && addressResult != null && errorMessage == null
}
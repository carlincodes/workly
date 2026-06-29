package com.example.workly.presentation.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "", // Usado apenas no Signup
    val isPasswordVisible: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val destinationRoute: String? = null
)
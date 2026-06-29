package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.presentation.auth.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(emailInput = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPasswordInput = password, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Simulação de login por enquanto
            kotlinx.coroutines.delay(1000)
            if (_uiState.value.emailInput.contains("@") && _uiState.value.passwordInput.length >= 6) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "E-mail ou senha inválidos") }
            }
        }
    }

    fun onSignupClicked() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.passwordInput != state.confirmPasswordInput) {
                _uiState.update { it.copy(errorMessage = "As senhas não coincidem") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Simulação de cadastro por enquanto
            kotlinx.coroutines.delay(1000)
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }
}

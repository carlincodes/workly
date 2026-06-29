package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.User
import com.example.workly.presentation.auth.AuthUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
            val state = _uiState.value
            val email = state.emailInput.trim()
            val password = state.passwordInput

            if (email.isBlank() || password.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Preencha e-mail e senha") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, destinationRoute = null) }

            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid

                if (uid == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Não foi possível identificar o usuário logado"
                        )
                    }
                    return@launch
                }

                val userSnapshot = firestore.collection("users").document(uid).get().await()
                val isProvider = userSnapshot.getBoolean("isProvider") ?: false
                val destination = if (isProvider) "provider_home" else "client_home"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        destinationRoute = destination,
                        errorMessage = null,
                        passwordInput = "",
                        confirmPasswordInput = ""
                    )
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Usuário não encontrado") }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "E-mail ou senha inválidos") }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Falha ao realizar login"
                    )
                }
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

            if (state.passwordInput.length < 6) {
                _uiState.update { it.copy(errorMessage = "A senha deve ter pelo menos 6 caracteres") }
                return@launch
            }

            val email = state.emailInput.trim()
            val password = state.passwordInput

            if (email.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Informe um e-mail válido") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, destinationRoute = null) }

            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid

                if (uid != null) {
                    val user = User(
                        id = uid,
                        email = email,
                        isProvider = false
                    )
                    firestore.collection("users").document(uid).set(user).await()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null,
                        passwordInput = "",
                        confirmPasswordInput = ""
                    )
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Este e-mail já está cadastrado") }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "E-mail inválido") }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Falha ao cadastrar usuário"
                    )
                }
            }
        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false, destinationRoute = null) }
    }
}

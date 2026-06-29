package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ProfileData
import com.example.workly.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.example.workly.presentation.profile.ProfileUiState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val data = repository.fetchProfileData()
                _uiState.update { it.copy(profileData = data ?: ProfileData(), errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao carregar perfil") }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveProfile(profileData: ProfileData) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val success = repository.saveProfileData(profileData)
                _uiState.update { it.copy(errorMessage = if (success) null else "Falha ao salvar perfil") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao salvar perfil") }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onProfileDataChanged(profileData: ProfileData) {
        _uiState.update { it.copy(profileData = profileData) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

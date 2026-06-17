package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ProfileData
import com.example.workly.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    private val _profile = MutableStateFlow(ProfileData())
    val profile: StateFlow<ProfileData> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.fetchProfileData()
                _profile.value = data ?: ProfileData()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar perfil"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveProfile(profileData: ProfileData) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.saveProfileData(profileData)
                _errorMessage.value = if (success) null else "Falha ao salvar perfil"
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao salvar perfil"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

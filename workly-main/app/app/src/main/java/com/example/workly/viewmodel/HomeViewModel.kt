package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.presentation.home.HomeUiState
import com.example.workly.presentation.home.ProviderHomeUiState
import com.example.workly.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = ServiceRepository()

    private val _clientUiState = MutableStateFlow(HomeUiState())
    val clientUiState: StateFlow<HomeUiState> = _clientUiState.asStateFlow()

    private val _providerUiState = MutableStateFlow(ProviderHomeUiState())
    val providerUiState: StateFlow<ProviderHomeUiState> = _providerUiState.asStateFlow()

    fun loadClientServices() {
        viewModelScope.launch {
            _clientUiState.update { it.copy(isLoading = true) }
            try {
                val services = repository.getServices()
                _clientUiState.update { it.copy(services = services, errorMessage = null) }
            } catch (e: Exception) {
                _clientUiState.update { it.copy(errorMessage = "Erro ao carregar serviços") }
            } finally {
                _clientUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadProviderServices() {
        viewModelScope.launch {
            _providerUiState.update { it.copy(isLoading = true) }
            try {
                val services = repository.getAvailableServices()
                _providerUiState.update { it.copy(availableServices = services, errorMessage = null) }
            } catch (e: Exception) {
                _providerUiState.update { it.copy(errorMessage = "Erro ao carregar serviços disponíveis") }
            } finally {
                _providerUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _clientUiState.update { it.copy(searchQuery = query) }
        _providerUiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: String) {
        _clientUiState.update { it.copy(selectedCategory = category) }
        _providerUiState.update { it.copy(selectedCategory = category) }
    }
}

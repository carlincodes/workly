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

class HomeViewModel(
    private val repository: ServiceRepository
) : ViewModel() {

    private val _clientUiState = MutableStateFlow(HomeUiState())
    val clientUiState: StateFlow<HomeUiState> =
        _clientUiState.asStateFlow()

    private val _providerUiState = MutableStateFlow(ProviderHomeUiState())
    val providerUiState: StateFlow<ProviderHomeUiState> =
        _providerUiState.asStateFlow()

    init {
        loadClientServices()
        loadProviderServices()
    }

    fun loadClientServices() {
        viewModelScope.launch {

            _clientUiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {

                val services = repository.getServices()

                _clientUiState.update {
                    it.copy(
                        services = services,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _clientUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao carregar serviços."
                    )
                }

            }
        }
    }

    fun loadProviderServices() {

        viewModelScope.launch {

            _providerUiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {

                val services = repository.getAvailableServices()

                _providerUiState.update {
                    it.copy(
                        availableServices = services,
                        isLoading = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _providerUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao carregar serviços."
                    )
                }

            }
        }
    }

    fun onSearchQueryChanged(query: String) {

        _clientUiState.update {
            it.copy(searchQuery = query)
        }

        _providerUiState.update {
            it.copy(searchQuery = query)
        }
    }

    fun onCategorySelected(category: String) {

        _clientUiState.update {
            it.copy(selectedCategory = category)
        }

        _providerUiState.update {
            it.copy(selectedCategory = category)
        }
    }
}
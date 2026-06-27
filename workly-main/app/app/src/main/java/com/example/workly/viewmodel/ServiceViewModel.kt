package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ServiceItem
import com.example.workly.repository.ServiceRepository
import com.example.workly.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceViewModel : ViewModel() {
    private val repository = ServiceRepository()
    private val apiRepository = ApiRepository()

    private val _userServices = MutableStateFlow<List<ServiceItem>>(emptyList())
    val userServices: StateFlow<List<ServiceItem>> = _userServices

    private val _availableServices = MutableStateFlow<List<ServiceItem>>(emptyList())
    val availableServices: StateFlow<List<ServiceItem>> = _availableServices

    private val _apiServices = MutableStateFlow<List<ServiceItem>>(emptyList())
    val apiServices: StateFlow<List<ServiceItem>> = _apiServices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadUserServices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val services = repository.getServices()
                _userServices.value = services
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar serviços"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAvailableServices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val services = repository.getAvailableServices()
                _availableServices.value = services
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar serviços disponíveis"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadServicesFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val services = apiRepository.getServicesFromApi()
                _apiServices.value = services
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar serviços da API"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createService(service: ServiceItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.createService(service)
                if (success) {
                    loadUserServices()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Erro ao criar serviço"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateService(oldTitle: String, updatedService: ServiceItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.updateService(oldTitle, updatedService)
                if (success) {
                    loadUserServices()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Erro ao atualizar serviço"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteService(serviceTitle: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.deleteService(serviceTitle)
                if (success) {
                    loadUserServices()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Erro ao deletar serviço"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
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

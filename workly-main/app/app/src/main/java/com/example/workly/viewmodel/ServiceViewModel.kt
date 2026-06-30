package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ServiceItem
import com.example.workly.repository.ServiceRepository
import com.example.workly.repository.ApiRepository
import com.example.workly.presentation.service.CreateServiceUiState
import com.example.workly.presentation.service.ServiceManagementUiState
import com.example.workly.presentation.api.ApiServicesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceViewModel(

    private val repository: ServiceRepository,
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _createServiceUiState = MutableStateFlow(CreateServiceUiState())
    val createServiceUiState: StateFlow<CreateServiceUiState> = _createServiceUiState.asStateFlow()

    private val _managementUiState = MutableStateFlow(ServiceManagementUiState())
    val managementUiState: StateFlow<ServiceManagementUiState> = _managementUiState.asStateFlow()

    private val _apiUiState = MutableStateFlow(ApiServicesUiState())
    val apiUiState: StateFlow<ApiServicesUiState> = _apiUiState.asStateFlow()

    fun onTitleChanged(title: String) {
        _createServiceUiState.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(description: String) {
        _createServiceUiState.update { it.copy(description = description) }
    }

    fun onJobTypeSelected(jobType: String) {
        _createServiceUiState.update { it.copy(selectedJobType = jobType, isJobTypeExpanded = false) }
    }

    fun onDropdownToggled(expanded: Boolean) {
        _createServiceUiState.update { it.copy(isJobTypeExpanded = expanded) }
    }

    fun onSaveClicked() {
        val state = _createServiceUiState.value
        val service = ServiceItem(
            title = state.title,
            description = state.description,
            category = state.selectedJobType,
            buttonText = "Ver Detalhes"
        )
        createService(service)
    }

    fun resetCreateServiceState() {
        _createServiceUiState.value = CreateServiceUiState()
    }

    fun onEditClicked(service: ServiceItem) {
        _managementUiState.update { 
            it.copy(
                selectedService = service,
                editTitle = service.title,
                editDescription = service.description,
                editCategory = service.category,
                showEditDialog = true
            ) 
        }
    }

    fun onDeleteClicked(service: ServiceItem) {
        deleteService(service.title)
    }

    fun onEditTitleChanged(title: String) {
        _managementUiState.update { it.copy(editTitle = title) }
    }

    fun onEditDescriptionChanged(description: String) {
        _managementUiState.update { it.copy(editDescription = description) }
    }

    fun onEditCategoryChanged(category: String) {
        _managementUiState.update { it.copy(editCategory = category) }
    }

    fun onSaveEditClicked() {
        val state = _managementUiState.value
        val selected = state.selectedService ?: return
        val updated = ServiceItem(
            title = state.editTitle,
            description = state.editDescription,
            category = state.editCategory,
            buttonText = selected.buttonText
        )
        updateService(selected.title, updated)
        onDismissDialog()
    }

    fun onDismissDialog() {
        _managementUiState.update { it.copy(showEditDialog = false, selectedService = null) }
    }

    fun onCepChanged(cep: String) {
        _apiUiState.update { it.copy(cepInput = cep) }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            _apiUiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Simulação de busca na API
                kotlinx.coroutines.delay(1000)
                _apiUiState.update { it.copy(addressResult = "Endereço para o CEP ${_apiUiState.value.cepInput}") }
            } catch (e: Exception) {
                _apiUiState.update { it.copy(errorMessage = "Erro ao buscar CEP") }
            } finally {
                _apiUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadUserServices() {
        viewModelScope.launch {
            _managementUiState.update { it.copy(isLoading = true) }
            try {
                val services = repository.getServices()
                _managementUiState.update { it.copy(userServices = services, errorMessage = null) }
            } catch (e: Exception) {
                _managementUiState.update { it.copy(errorMessage = "Erro ao carregar serviços") }
            } finally {
                _managementUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun createService(service: ServiceItem) {
        viewModelScope.launch {
            _createServiceUiState.update { it.copy(isLoading = true) }
            try {
                val success = repository.createService(service)
                if (success) {
                    _createServiceUiState.update { it.copy(isServiceCreated = true, errorMessage = null) }
                    loadUserServices()
                } else {
                    _createServiceUiState.update { it.copy(errorMessage = "Erro ao criar serviço") }
                }
            } catch (e: Exception) {
                _createServiceUiState.update { it.copy(errorMessage = "Erro: ${e.message}") }
            } finally {
                _createServiceUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateService(oldTitle: String, updatedService: ServiceItem) {
        viewModelScope.launch {
            _managementUiState.update { it.copy(isLoading = true) }
            try {
                val success = repository.updateService(oldTitle, updatedService)
                if (success) {
                    loadUserServices()
                } else {
                    _managementUiState.update { it.copy(errorMessage = "Erro ao atualizar serviço") }
                }
            } catch (e: Exception) {
                _managementUiState.update { it.copy(errorMessage = "Erro: ${e.message}") }
            } finally {
                _managementUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteService(serviceTitle: String) {
        viewModelScope.launch {
            _managementUiState.update { it.copy(isLoading = true) }
            try {
                val success = repository.deleteService(serviceTitle)
                if (success) {
                    loadUserServices()
                } else {
                    _managementUiState.update { it.copy(errorMessage = "Erro ao deletar serviço") }
                }
            } catch (e: Exception) {
                _managementUiState.update { it.copy(errorMessage = "Erro: ${e.message}") }
            } finally {
                _managementUiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

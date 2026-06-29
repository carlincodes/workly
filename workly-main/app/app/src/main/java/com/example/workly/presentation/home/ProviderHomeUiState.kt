package com.example.workly.presentation.home

import com.example.workly.model.ServiceItem

data class ProviderHomeUiState(
    val isLoading: Boolean = false,
    val availableServices: List<ServiceItem> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "Todos"
) {
    // Validação limpa para o estado de lista vazia de serviços disponíveis
    val isListEmpty: Boolean get() = !isLoading && availableServices.isEmpty() && errorMessage == null
}
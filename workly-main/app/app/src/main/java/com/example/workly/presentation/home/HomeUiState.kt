package com.example.workly.presentation.home

import com.example.workly.model.ServiceItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val services: List<ServiceItem> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "Todos"
) {
    // Propriedade utilitária para validar de forma limpa o estado de lista vazia
    val isListEmpty: Boolean get() = !isLoading && services.isEmpty() && errorMessage == null
}
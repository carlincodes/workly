package com.example.workly.presentation.service

import com.example.workly.model.ServiceItem

data class ServiceManagementUiState(
    val isLoading: Boolean = false,
    val userServices: List<ServiceItem> = emptyList(),
    val errorMessage: String? = null,

    // Estados do diálogo de edição
    val showEditDialog: Boolean = false,
    val selectedService: ServiceItem? = null,
    val editTitle: String = "",
    val editDescription: String = "",
    val editCategory: String = ""
) {
    val isListEmpty: Boolean get() = !isLoading && userServices.isEmpty() && errorMessage == null
}
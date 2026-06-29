package com.example.workly.presentation.service

data class CreateServiceUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val selectedJobType: String = "Eletricista",
    val isJobTypeExpanded: Boolean = false,
    val errorMessage: String? = null,
    val isServiceCreated: Boolean = false
)
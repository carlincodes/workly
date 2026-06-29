package com.example.workly.presentation.profile

import com.example.workly.model.ProfileData

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profileData: ProfileData = ProfileData(),
    val errorMessage: String? = null,
    val isEditMode: Boolean = false
)
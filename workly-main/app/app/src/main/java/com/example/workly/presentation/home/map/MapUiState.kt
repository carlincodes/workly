package com.example.workly.presentation.map

import com.example.workly.model.ProviderLocationInfo
import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val isLoading: Boolean = false,
    val userLocation: LatLng = LatLng(-7.119495, -34.845011),
    val providersNearby: List<ProviderLocationInfo> = emptyList(),
    val searchRadiusMeters: Float = 5000f,
    val errorMessage: String? = null
) {
    val hasProviders: Boolean get() = !isLoading && providersNearby.isNotEmpty()
}
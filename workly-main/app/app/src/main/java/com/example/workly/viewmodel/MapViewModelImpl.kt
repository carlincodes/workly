package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.model.ProviderLocationInfo
import com.example.workly.repository.LocationRepository
import com.example.workly.service.LocationUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

   
                                           
                                                     
   
import com.example.workly.presentation.map.MapUiState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.android.gms.maps.model.LatLng

class MapViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun startLocationTracking() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                locationRepository.getCurrentLocation().collect { location ->
                    _uiState.update { it.copy(userLocation = LatLng(location.latitude, location.longitude)) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Erro ao obter localização") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onRadiusChanged(radiusMeters: Float) {
        _uiState.update { it.copy(searchRadiusMeters = radiusMeters) }
    }

    fun onProviderClicked(provider: ProviderLocationInfo) {
        // Handle provider click
    }

       
                               
       
    fun setSearchRadius(radiusMeters: Float) {
        _uiState.update { it.copy(searchRadiusMeters = radiusMeters) }
    }

       
                                       
                                                                                 
       
    fun filterProvidersByDistance(
        allProviders: List<ProviderLocationInfo>,
        radiusMeters: Float = _uiState.value.searchRadiusMeters
    ) {
        val current = _uiState.value.userLocation

        val filtered = allProviders.filter { provider ->
            val distance = calculateDistance(
                current.latitude, current.longitude,
                provider.latitude, provider.longitude
            )
            distance <= radiusMeters
        }.map { provider ->
            provider.copy(
                distance = calculateDistance(
                    current.latitude, current.longitude,
                    provider.latitude, provider.longitude
                )
            )
        }.sortedBy { it.distance }

        _uiState.update { it.copy(providersNearby = filtered) }
    }

       
                                                                 
       
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val earthRadiusMeters = 6371000f             
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadiusMeters * c).toFloat()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

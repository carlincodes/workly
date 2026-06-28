package com.example.workly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workly.WorklyApp
import com.example.workly.model.ProviderLocationInfo
import com.example.workly.repository.LocationRepository
import com.example.workly.service.LocationService
import com.example.workly.service.LocationUpdate
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

   
                                           
                                                     
   
class MapViewModel(
    private val locationRepository: LocationRepository = LocationRepository(
        LocationService(
            context = WorklyApp.instance,
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(WorklyApp.instance)
        )
    )
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<LocationUpdate?>(null)
    val currentLocation: StateFlow<LocationUpdate?> = _currentLocation

    private val _providersNearby = MutableStateFlow<List<ProviderLocationInfo>>(emptyList())
    val providersNearby: StateFlow<List<ProviderLocationInfo>> = _providersNearby

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchRadius = MutableStateFlow(5000f)              
    val searchRadius: StateFlow<Float> = _searchRadius

       
                                           
       
    fun startLocationTracking() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                locationRepository.getCurrentLocation().collect { location ->
                    _currentLocation.value = location
                    loadNearbyProviders()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao obter localização"
            } finally {
                _isLoading.value = false
            }
        }
    }

       
                               
       
    fun setSearchRadius(radiusMeters: Float) {
        _searchRadius.value = radiusMeters
        loadNearbyProviders()
    }

    fun loadNearbyProviders() {
        val current = _currentLocation.value ?: return
        val availableProviders = sampleProviders()
        filterProvidersByDistance(availableProviders, _searchRadius.value)
        _currentLocation.value = current
    }

       
                                       
                                                                                 
       
    fun filterProvidersByDistance(
        allProviders: List<ProviderLocationInfo>,
        radiusMeters: Float = _searchRadius.value
    ) {
        val current = _currentLocation.value ?: return

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

        _providersNearby.value = filtered
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
        _error.value = null
    }

    private fun sampleProviders(): List<ProviderLocationInfo> {
        val current = _currentLocation.value
        val baseLatitude = current?.latitude ?: -23.550520
        val baseLongitude = current?.longitude ?: -46.633308

        return listOf(
            ProviderLocationInfo(
                providerId = "provider-1",
                name = "Ana Eletricista",
                specialty = "Eletricista",
                latitude = baseLatitude + 0.010,
                longitude = baseLongitude + 0.008
            ),
            ProviderLocationInfo(
                providerId = "provider-2",
                name = "Carlos Encanador",
                specialty = "Encanador",
                latitude = baseLatitude - 0.012,
                longitude = baseLongitude + 0.004
            ),
            ProviderLocationInfo(
                providerId = "provider-3",
                name = "Marina Pintora",
                specialty = "Pintora",
                latitude = baseLatitude + 0.006,
                longitude = baseLongitude - 0.009
            )
        )
    }
}

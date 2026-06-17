package com.example.workly.repository

import com.example.workly.service.LocationService
import com.example.workly.service.LocationUpdate
import kotlinx.coroutines.flow.Flow

   
                                    
                                                                 
   
class LocationRepository(private val locationService: LocationService) {

    fun getCurrentLocation(): Flow<LocationUpdate> {
        return locationService.getCurrentLocation()
    }

    suspend fun getLastKnownLocation(): LocationUpdate? {
        return locationService.getLastKnownLocation()
    }
}

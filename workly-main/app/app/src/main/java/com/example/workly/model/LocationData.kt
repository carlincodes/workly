package com.example.workly.model

   
                                    
                                         
   
data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: Float = 0f
)

   
                                                       
   
data class ProviderLocationInfo(
    val providerId: String = "",
    val name: String = "",
    val specialty: String = "",
    val rating: Float = 0f,
    val profileImageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distance: Float = 0f             
)

package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.workly.viewmodel.MapViewModel
import com.example.workly.model.ProviderLocationInfo

   
                   
                                                          
   
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val viewModel: MapViewModel = viewModel()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val providersNearby by viewModel.providersNearby.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchRadius by viewModel.searchRadius.collectAsState()
    
    val centerLocation = currentLocation?.let {
        LatLng(it.latitude, it.longitude)
    } ?: LatLng(-23.550520, -46.633308)                    
    
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.CameraPosition.fromLatLngZoom(centerLocation, 14f)
    }

    LaunchedEffect(Unit) {
        viewModel.startLocationTracking()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prestadores Próximos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                             
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f),
                    cameraPositionState = cameraPositionState
                ) {
                                                       
                    currentLocation?.let {
                        Marker(
                            state = MarkerState(position = centerLocation),
                            title = "Sua Localização",
                            snippet = "Você está aqui"
                        )
                    }

                                               
                    Circle(
                        center = centerLocation,
                        radius = searchRadius.toDouble(),
                        fillColor = android.graphics.Color.argb(30, 33, 150, 243),
                        strokeColor = android.graphics.Color.argb(100, 33, 150, 243),
                        strokeWidth = 2f
                    )

                                              
                    providersNearby.forEach { provider ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(provider.latitude, provider.longitude)
                            ),
                            title = provider.name,
                            snippet = provider.specialty
                        )
                    }
                }

                                     
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                                                  
                        Text(
                            text = "Raio de busca: ${(searchRadius / 1000).toInt()} km",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Slider(
                            value = searchRadius / 1000f,
                            onValueChange = { viewModel.setSearchRadius(it * 1000f) },
                            valueRange = 1f..15f,
                            steps = 14,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                                               
                        Text(
                            text = "Prestadores próximos (${providersNearby.size})",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        providersNearby.forEach { provider ->
                            ProviderCard(
                                provider = provider,
                                onClick = {
                                                                        
                                    navController.navigate("chat/${provider.providerId}/${provider.name}")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (providersNearby.isEmpty()) {
                            Text(
                                text = "Nenhum prestador encontrado neste raio",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

   
                                            
   
@Composable
fun ProviderCard(
    provider: ProviderLocationInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = provider.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = provider.specialty,
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${(provider.distance / 1000).toInt()} km",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            Button(
                onClick = onClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Contatar")
            }
        }
    }
}

    val radiusMeters = (radiusKm * 1000).toInt()
    val filteredProviders = providers.filter { provider ->
        haversineDistance(center.latitude, center.longitude, provider.location.latitude, provider.location.longitude) <= radiusKm
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Prestadores") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {                                }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Localização")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Raio atual: ${radiusKm.toInt()} km",
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                fontSize = 16.sp
            )
            Slider(
                value = radiusKm,
                onValueChange = { radiusKm = it.coerceIn(1f, 10f) },
                valueRange = 1f..10f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .padding(16.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = com.google.maps.android.compose.MapProperties(isMyLocationEnabled = false)
                ) {
                    Circle(
                        center = center,
                        radius = radiusMeters.toDouble(),
                        fillColor = 0x220066CC,
                        strokeColor = 0x660066CC,
                        strokeWidth = 2f
                    )
                    filteredProviders.forEach { provider ->
                        Marker(
                            state = MarkerState(position = provider.location),
                            title = provider.name,
                            snippet = provider.specialty
                        )
                    }
                }
            }
            Text(
                text = "Prestadores no raio: ${filteredProviders.size}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            filteredProviders.forEach { provider ->
                Text(
                    text = "• ${provider.name} — ${provider.specialty}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private data class ProviderLocation(
    val name: String,
    val location: LatLng,
    val specialty: String
)

private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val earthRadius = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2).pow(2.0) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2).pow(2.0)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return (earthRadius * c).toFloat()
}

private fun Double.pow(exponent: Double): Double = kotlin.math.pow(exponent)

@Preview(showBackground = true, heightDp = 900)
@Composable
fun MapScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    MaterialTheme {
        MapScreen(navController)
    }
}

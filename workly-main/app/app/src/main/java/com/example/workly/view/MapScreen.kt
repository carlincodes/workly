package com.example.workly.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.model.ProviderLocationInfo
import com.example.workly.presentation.map.MapUiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    uiState: MapUiState,
    onRadiusChanged: (Float) -> Unit,
    onProviderClicked: (ProviderLocationInfo) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.userLocation, 13f)
    }

    val userMarkerState = rememberMarkerState(position = uiState.userLocation)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prestadores Próximos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
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
                    Marker(
                        state = userMarkerState,
                        title = "Sua Localização",
                        snippet = "Você está aqui"
                    )

                    Circle(
                        center = uiState.userLocation,
                        radius = uiState.searchRadiusMeters.toDouble(),
                        fillColor = Color(0x1E2196F3),
                        strokeColor = Color(0x642196F3),
                        strokeWidth = 2f
                    )

                    uiState.providersNearby.forEach { provider ->
                        Marker(
                            state = rememberMarkerState(
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
                            text = "Raio de busca: ${(uiState.searchRadiusMeters / 1000).toInt()} km",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Slider(
                            value = uiState.searchRadiusMeters / 1000f,
                            onValueChange = { onRadiusChanged(it * 1000f) },
                            valueRange = 1f..15f,
                            steps = 13,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Prestadores próximos encontrados (${uiState.providersNearby.size})",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.hasProviders) {
                            uiState.providersNearby.forEach { provider ->
                                ProviderCard(
                                    provider = provider,
                                    onClick = { onProviderClicked(provider) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        } else if (!uiState.isLoading) {
                            Text(
                                text = "Nenhum prestador encontrado neste raio.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
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
            .clickable(onClick = onClick),
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
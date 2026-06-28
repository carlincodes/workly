package com.example.workly.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workly.model.ProviderLocationInfo
import com.example.workly.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

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
        position = CameraPosition.fromLatLngZoom(centerLocation, 14f)
    }

    LaunchedEffect(Unit) {
        viewModel.startLocationTracking()
    }

    LaunchedEffect(centerLocation, searchRadius) {
        viewModel.loadNearbyProviders()
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
                        fillColor = Color(0x1E2196F3),
                        strokeColor = Color(0x802196F3),
                        strokeWidth = 2f
                    )

                    providersNearby.forEach { provider ->
                        Marker(
                            state = MarkerState(position = LatLng(provider.latitude, provider.longitude)),
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
                            steps = 13,
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
                                onClick = { navController.navigate("chat") }
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun ProviderCard(
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
                        imageVector = Icons.Default.LocationOn,
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

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun MapScreenPreview() {
    MaterialTheme {
        MapScreen(navController = rememberNavController())
    }
}

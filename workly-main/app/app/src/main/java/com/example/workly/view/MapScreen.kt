package com.example.workly.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.model.ProviderLocationInfo
import com.example.workly.presentation.map.MapUiState
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    uiState: MapUiState, // Estado imutável controlado externamente
    onRadiusChanged: (Float) -> Unit, // Evento de arrastar o Slider de raio
    onProviderClicked: (ProviderLocationInfo) -> Unit // Evento de clique para abrir chat com prestador
) {
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.CameraPosition.fromLatLngZoom(uiState.userLocation, 13f)
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

                // Renderização do Google Maps integrada ao Estado
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f),
                    cameraPositionState = cameraPositionState
                ) {
                    // Marcador da posição atual do usuário
                    Marker(
                        state = MarkerState(position = uiState.userLocation),
                        title = "Sua Localização",
                        snippet = "Você está aqui"
                    )

                    // Círculo visual do raio de busca
                    Circle(
                        center = uiState.userLocation,
                        radius = uiState.searchRadiusMeters.toDouble(),
                        fillColor = android.graphics.Color.argb(30, 33, 150, 243),
                        strokeColor = android.graphics.Color.argb(100, 33, 150, 243),
                        strokeWidth = 2f
                    )

                    // Marcadores dinâmicos dos prestadores encontrados no raio
                    uiState.providersNearby.forEach { provider ->
                        Marker(
                            state = MarkerState(
                                position = com.google.android.gms.maps.model.LatLng(provider.latitude, provider.longitude)
                            ),
                            title = provider.name,
                            snippet = provider.specialty
                        )
                    }
                }

                // Painel Inferior de Controle e Listagem de Itens
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
                            steps = 14,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Prestadores próximos encontrados (${uiState.providersNearby.size})",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Lista os prestadores que estão no Estado de Sucesso
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

            // Estado de LOADING sobreposto
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
package com.example.workly.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val sp = LatLng(-23.5505, -46.6333) // São Paulo
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sp, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prestadores Próximos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = sp),
                title = "Você está aqui",
                snippet = "Sua localização"
            )
            
            // Simulação de prestadores (RF02)
            Marker(
                state = MarkerState(position = LatLng(-23.56, -46.64)),
                title = "João - Eletricista",
                snippet = "A 2km de distância"
            )
            Marker(
                state = MarkerState(position = LatLng(-23.54, -46.62)),
                title = "Maria - Encanadora",
                snippet = "A 1.5km de distância"
            )
        }
    }
}

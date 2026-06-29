package com.example.workly.view

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.components.ServiceCard
import com.example.workly.presentation.home.ProviderHomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderHomeScreen(
    navController: NavController,
    uiState: ProviderHomeUiState, // Estado imutável controlado externamente
    onSearchQueryChanged: (String) -> Unit, // Evento disparado ao digitar na busca
    onCategorySelected: (String) -> Unit   // Evento disparado ao selecionar um filtro chip
) {
    val filterCategories = listOf("Todos", "Eletricista", "Encanador", "Pintor")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Serviços disponíveis") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu / Perfil")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("map") }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa")
                    }
                    IconButton(onClick = { navController.navigate("chat") }) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Chat")
                    }
                    IconButton(onClick = { navController.navigate("api_services") }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar na API")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. Tratamento do Estado de LOADING
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Campo de busca reativo
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Buscar serviços") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                // Filtros de categoria
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterCategories.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category) }
                        )
                    }
                }

                // 2. Tratamento do Estado de ERROR
                if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.errorMessage, color = Color.Red)
                    }
                }
                // 3. Tratamento do Estado de LISTA VAZIA
                else if (uiState.isListEmpty) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Nenhum serviço disponível no momento.")
                    }
                }
                // 4. Tratamento do Estado de SUCESSO
                else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.availableServices) { service ->
                            ServiceCard(
                                title = service.title,
                                description = service.description,
                                category = service.category,
                                buttonText = service.buttonText,
                                onClick = {
                                    navController.navigate(
                                        "service_detail/provider/${Uri.encode(service.category)}/${Uri.encode(service.title)}?description=${Uri.encode(service.description)}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
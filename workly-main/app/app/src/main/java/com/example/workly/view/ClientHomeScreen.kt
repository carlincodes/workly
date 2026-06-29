package com.example.workly.view

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.components.ServiceCard
import com.example.workly.presentation.home.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    navController: NavController,
    uiState: HomeUiState, // Estado imutável fornecido de forma reativa externa
    onSearchQueryChanged: (String) -> Unit, // Evento de alteração no campo de busca
    onCategorySelected: (String) -> Unit // Evento de clique nos filtros de categoria
) {
    val filterCategories = listOf("Todos", "Eletricista", "Encanador", "Pintor")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Serviços") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("service_management") }) {
                Text("+")
            }
        }
    ) { padding ->

        // 1. Tratamento do Estado de LOADING (Carregamento)
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Campo de pesquisa lê do estado imutável e repassa o evento para fora
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    label = { Text("Buscar serviços") }
                )

                // Barra horizontal de Chips de categorias
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

                // 2. Tratamento do Estado de ERROR (Erro de Conexão/API)
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
                        Text(text = "Nenhum serviço encontrado para esta categoria.")
                    }
                }
                // 4. Tratamento do Estado de SUCESSO (Lista populada)
                else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.services) { service ->
                            ServiceCard(
                                title = service.title,
                                description = service.description,
                                category = service.category,
                                buttonText = service.buttonText,
                                onClick = {
                                    navController.navigate(
                                        "service_detail/client/${Uri.encode(service.category)}/${Uri.encode(service.title)}?description=${Uri.encode(service.description)}"
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
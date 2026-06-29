package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.presentation.api.ApiServicesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiServicesScreen(
    navController: NavController,
    uiState: ApiServicesUiState, // Estado imutável fornecido pelo exterior
    onCepChanged: (String) -> Unit, // Evento disparado ao digitar o CEP
    onSearchClicked: () -> Unit // Evento de clique para disparar a busca na API
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Endereço via API") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Insira o CEP para autocompletar a sua localização profissional",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Campo de input lê do estado e repassa as alterações
            OutlinedTextField(
                value = uiState.cepInput,
                onValueChange = onCepChanged,
                label = { Text("CEP") },
                placeholder = { Text("Ex: 58000000") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = onSearchClicked,
                        enabled = uiState.cepInput.isNotBlank() && !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                enabled = !uiState.isLoading
            )

            // 1. Tratamento do Estado de LOADING
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // 2. Tratamento do Estado de ERRO
            else if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            // 3. Tratamento do Estado de SUCESSO (Resultado da API)
            else if (uiState.hasResult && uiState.addressResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Resultado Encontrado:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = uiState.addressResult,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                // Estado neutro/vazio antes da busca
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aguardando consulta...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.model.ServiceItem
import com.example.workly.presentation.service.ServiceManagementUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceManagementScreen(
    navController: NavController,
    uiState: ServiceManagementUiState, // Estado centralizado imutável
    onEditClicked: (ServiceItem) -> Unit, // Usuário clicou no ícone de editar do card
    onDeleteClicked: (ServiceItem) -> Unit, // Usuário clicou no ícone de deletar do card
    onEditTitleChanged: (String) -> Unit, // Mudança de texto no diálogo
    onEditDescriptionChanged: (String) -> Unit, // Mudança de texto no diálogo
    onEditCategoryChanged: (String) -> Unit, // Mudança de texto no diálogo
    onSaveEditClicked: () -> Unit, // Usuário clicou em Salvar no diálogo
    onDismissDialog: () -> Unit // Usuário cancelou ou fechou o diálogo
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Serviços") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_service") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Tratamento do Estado de LOADING
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // 2. Tratamento do Estado de LISTA VAZIA
            else if (uiState.isListEmpty) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum serviço criado ainda")
                }
            }
            // 3. Tratamento do Estado de SUCESSO (Lista populada)
            else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.userServices) { service ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = service.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = service.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = service.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = { onEditClicked(service) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { onDeleteClicked(service) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Deletar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 4. Diálogo de edição controlado reativamente por propriedades do Estado
    if (uiState.showEditDialog && uiState.selectedService != null) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Editar Serviço") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.editTitle,
                        onValueChange = onEditTitleChanged,
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.editDescription,
                        onValueChange = onEditDescriptionChanged,
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = uiState.editCategory,
                        onValueChange = onEditCategoryChanged,
                        label = { Text("Categoria") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = onSaveEditClicked) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("Cancelar")
                }
            }
        )
    }
}
package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.workly.model.Service
import com.example.workly.repository.ServiceRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(navController: NavController) {

    val repository = remember { ServiceRepository() }
    var services by remember { mutableStateOf(emptyList<Service>()) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun loadServices() {
        scope.launch {
            val result = repository.getServices()
            services = result
        }
    }

    // Atualiza a lista sempre que a tela ganha foco (volta do popBackStack)
    val navStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navStackEntry) {
        loadServices()
    }

    val filteredServices = services.filter { service ->
        service.title.contains(searchQuery, ignoreCase = true) ||
        service.description.contains(searchQuery, ignoreCase = true) ||
        service.jobType.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Serviços") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("client_profile") }) {
                        Icon(Icons.Default.Menu, contentDescription = "Perfil")
                    }
                },
                actions = {
                    IconButton(onClick = { loadServices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                label = { Text("Buscar por título, descrição ou tipo") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)
            ) {
                items(filteredServices) { service ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = service.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = service.jobType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = service.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            repository.deleteService(service.id)
                                            loadServices()
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Excluir",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            val updated = service.copy(
                                                status = if (service.status == "OPEN") "IN_PROGRESS" else "CLOSED"
                                            )
                                            repository.updateService(updated)
                                            loadServices()
                                        }
                                    }
                                ) {
                                    Text(if (service.status == "OPEN") "Iniciar" else "Concluir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 1000
)

@Composable
fun ClientHomeScreenPreview() {

    val navController =
        rememberNavController()

    MaterialTheme {

        ClientHomeScreen(
            navController = navController
        )
    }
}
package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import com.example.workly.model.User
import com.example.workly.repository.ServiceRepository
import com.example.workly.repository.UserRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(navController: NavController) {

    val repository = remember { ServiceRepository() }
    val userRepository = remember { UserRepository() }
    
    var services by remember { mutableStateOf(emptyList<Service>()) }
    var searchQuery by remember { mutableStateOf("") }
    var currentUser by remember { mutableStateOf<User?>(null) }
    
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    fun loadData() {
        scope.launch {
            services = repository.getServices()
            currentUser = userRepository.getCurrentUser()
        }
    }

    val navStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navStackEntry) {
        loadData()
    }

    val filteredServices = services.filter { service ->
        service.title.contains(searchQuery, ignoreCase = true) ||
        service.description.contains(searchQuery, ignoreCase = true) ||
        service.jobType.contains(searchQuery, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentUser?.name ?: "Usuário",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = currentUser?.email ?: "carregando...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    NavigationDrawerItem(
                        label = { Text("Ver Mapa") },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            navController.navigate("map") 
                        },
                        icon = { Icon(Icons.Default.Map, contentDescription = null) }
                    )
                    
                    NavigationDrawerItem(
                        label = { Text("Meu Perfil") },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            navController.navigate("client_profile") 
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Meus Serviços") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { loadData() }) {
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
                                            navController.navigate("chat/${service.id}")
                                        }
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Chat,
                                            contentDescription = "Chat",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                repository.deleteService(service.id)
                                                loadData()
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
                                            navController.navigate(
                                                "edit_service/${service.id}"
                                            )
                                        }
                                    ) {
                                        Text("Editar")
                                    }
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
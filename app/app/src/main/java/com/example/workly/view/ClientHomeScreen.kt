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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workly.components.ServiceCard
import com.example.workly.model.ServiceItem
import com.example.workly.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(navController: NavController) {
    val viewModel: ServiceViewModel = viewModel()
    val userServices by viewModel.userServices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserServices()
    }

    val services = userServices.ifEmpty {
        listOf(
            ServiceItem(
                title = "Conserto de pia",
                description = "Preciso urgente, cozinha vazando",
                category = "Encanador",
                buttonText = "Ver detalhes"
            ),
            ServiceItem(
                title = "Instalar ventilador",
                description = "Quarto precisa de instalação rápida",
                category = "Eletricista",
                buttonText = "Ver detalhes"
            ),
            ServiceItem(
                title = "Pintar sala",
                description = "Sala pequena, pintura interna",
                category = "Pintor",
                buttonText = "Ver detalhes"
            )
        )
    }

    val filterCategories = listOf("Todos", "Eletricista", "Encanador", "Pintor")
    var selectedCategory by remember { mutableStateOf("Todos") }

    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredServices = services.filter { service ->
        val matchesQuery = service.title.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true) ||
                service.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Todos" || service.category == selectedCategory
        matchesQuery && matchesCategory
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Meus Serviços")
                },

                navigationIcon = {

                    IconButton(

                        onClick = {

                            navController.navigate(
                                "client_profile"
                            )
                        }

                    ) {

                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Perfil"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("map") }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa")
                    }
                    IconButton(onClick = { navController.navigate("chat") }) {
                        Icon(Icons.Default.ChatBubble, contentDescription = "Chat")
                    }
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(

                onClick = {

                    navController.navigate(
                        "service_management"
                    )
                }

            ) {

                Text("+")
            }
        }

    ) { padding ->

        if (isLoading) {
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
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {

            TextField(

                value = searchQuery,

                onValueChange = {
                    searchQuery = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),

                label = {
                    Text("Buscar serviços")
                },

                leadingIcon = {

                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterCategories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(12.dp),

                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)

            ) {

                items(filteredServices) { service ->
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
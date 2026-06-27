package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workly.model.ProfileData
import com.example.workly.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(navController: NavController) {

    val viewModel: ProfileViewModel = viewModel()
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var userName by rememberSaveable { mutableStateOf(profile.name) }
    var userEmail by rememberSaveable { mutableStateOf(profile.email) }
    var userPhone by rememberSaveable { mutableStateOf(profile.phone) }
    var userProfession by rememberSaveable { mutableStateOf(profile.profession) }
    var userDescription by rememberSaveable { mutableStateOf(profile.description) }

    LaunchedEffect(profile) {
        userName = profile.name
        userEmail = profile.email
        userPhone = profile.phone
        userProfession = profile.profession
        userDescription = profile.description
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(

        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,

        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,

        cursorColor = MaterialTheme.colorScheme.primary,

        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Meu Perfil")
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Perfil",

                        modifier = Modifier.size(72.dp),

                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Bem-vindo, $userName",

                        style = MaterialTheme.typography.titleMedium,

                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = userEmail,

                        style = MaterialTheme.typography.bodyMedium,

                        color = MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(

                text = "Dados do Perfil",

                style = MaterialTheme.typography.titleMedium,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(

                value = userName,

                onValueChange = {
                    userName = it
                },

                label = {
                    Text("Nome")
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),

                singleLine = true,

                colors = textFieldColors
            )

            OutlinedTextField(

                value = userEmail,

                onValueChange = {
                    userEmail = it
                },

                label = {
                    Text("Email")
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),

                singleLine = true,

                colors = textFieldColors
            )

            OutlinedTextField(

                value = userPhone,

                onValueChange = {
                    userPhone = it
                },

                label = {
                    Text("Telefone")
                },

                placeholder = {
                    Text("(83) 99999-9999")
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),

                singleLine = true,

                colors = textFieldColors
            )

            OutlinedTextField(
                value = userProfession,
                onValueChange = { userProfession = it },
                label = { Text("Profissão") },
                placeholder = { Text("Ex: Eletricista, Encanador, Pintor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                colors = textFieldColors
            )

            OutlinedTextField(
                value = userDescription,
                onValueChange = { userDescription = it },
                label = { Text("Descrição profissional") },
                placeholder = { Text("Apresente seus serviços e experiências") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                maxLines = 4,
                colors = textFieldColors
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(

                onClick = {
                    viewModel.saveProfile(
                        ProfileData(
                            name = userName,
                            email = userEmail,
                            phone = userPhone,
                            profession = userProfession,
                            description = userDescription
                        )
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                Text("Salvar alterações")
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedButton(

                onClick = {

                    navController.navigate("login") {

                        popUpTo(0)
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                Text("Sair da conta")
            }
        }
    }
}

@Preview(
    showBackground = true,
    heightDp = 800
)

@Composable
fun ClientProfileScreenPreview() {

    val navController = rememberNavController()

    MaterialTheme {

        ClientProfileScreen(
            navController = navController
        )
    }
}
package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.model.ProfileData
import com.example.workly.presentation.profile.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    uiState: ProfileUiState, // Estado reativo unificado
    onProfileDataChanged: (ProfileData) -> Unit, // Repassa mudanças nos campos para o ViewModel
    onSaveClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Bem vindo, ${uiState.profileData.name}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = uiState.profileData.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(text = "Dados do Perfil", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

                OutlinedTextField(
                    value = uiState.profileData.name,
                    onValueChange = { onProfileDataChanged(uiState.profileData.copy(name = it)) },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.profileData.email,
                    onValueChange = { onProfileDataChanged(uiState.profileData.copy(email = it)) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.profileData.profession,
                    onValueChange = { onProfileDataChanged(uiState.profileData.copy(profession = it)) },
                    label = { Text("Profissão") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.profileData.description,
                    onValueChange = { onProfileDataChanged(uiState.profileData.copy(description = it)) },
                    label = { Text("Descrição profissional") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onSaveClicked, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Salvar alterações")
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(onClick = onLogoutClicked, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Sair da conta")
                }
            }
        }
    }
}
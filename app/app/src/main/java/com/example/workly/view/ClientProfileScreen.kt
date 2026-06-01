package com.example.workly.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.workly.model.User
import com.example.workly.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreen(navController: NavController) {

    val repository = remember { UserRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("Nome") }
    var userEmail by remember { mutableStateOf("nome@email.com") }
    var userPhone by remember { mutableStateOf("(83) 99999-9999") } // Mantido o estado local do seu design

    var isProviderStatus by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // R do CRUD: Carrega os dados reais do Firestore ao entrar na tela
    LaunchedEffect(Unit) {
        try {
            val user = repository.getCurrentUser()
            if (user != null) {
                userName = user.name.orEmpty().ifBlank { "Nome" }
                userEmail = user.email.orEmpty().ifBlank { "nome@email.com" }
                isProviderStatus = user.isProvider
            } else {
                val localAuth = FirebaseAuth.getInstance().currentUser
                if (localAuth != null) {
                    userEmail = localAuth.email.orEmpty()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show()
        }
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
                title = { Text("Meu Perfil") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bem-vindo, $userName",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                onValueChange = { userName = it },
                label = { Text("Nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                colors = textFieldColors
            )

            OutlinedTextField(
                value = userEmail,
                onValueChange = { userEmail = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                enabled = false,
                colors = textFieldColors
            )

            OutlinedTextField(
                value = userPhone,
                onValueChange = { userPhone = it },
                label = { Text("Telefone") },
                placeholder = { Text("(83) 99999-9999") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // U do CRUD: Salva as alterações respeitando rigidamente o modelo User do grupo
            Button(
                onClick = {
                    val currentUid = FirebaseAuth.getInstance().currentUser?.uid

                    if (currentUid.isNullOrEmpty()) {
                        Toast.makeText(context, "Erro: Usuário não identificado.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (userName.isBlank()) {
                        Toast.makeText(context, "O nome não pode estar em branco.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSaving = true
                    scope.launch {
                        try {
                            val updatedUser = User(
                                id = currentUid,
                                name = userName,
                                email = userEmail,
                                isProvider = isProviderStatus
                            )

                            val success = repository.updateUser(updatedUser)

                            if (success) {
                                Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Erro ao salvar alterações.", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Falha de conexão.", Toast.LENGTH_SHORT).show()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isSaving && userName.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Salvar alterações")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
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

@Preview(showBackground = true, heightDp = 800)
@Composable
fun ClientProfileScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        ClientProfileScreen(navController = navController)
    }
}
package com.example.workly.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.model.ChatMessage
import com.example.workly.presentation.chat.ChatUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    uiState: ChatUiState, // Estado unificado enviado externamente
    onMessageTextChanged: (String) -> Unit, // Evento de digitação
    onSendMessageClicked: () -> Unit, // Evento de clique no botão Enviar
    onErrorDismissed: () -> Unit // Evento para fechar aviso de erro
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.chatPartnerName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Lista de mensagens baseada no estado de Sucesso
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    items(uiState.messages.reversed()) { message ->
                        MessageBubble(message)
                    }
                }

                if (uiState.isChatEmpty) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Diga olá! Nenhuma mensagem por aqui ainda.", color = Color.Gray)
                    }
                }

                Divider()

                // Campo de entrada de texto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = uiState.messageInput,
                        onValueChange = onMessageTextChanged,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 40.dp, max = 100.dp),
                        placeholder = { Text("Digite uma mensagem...") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = { if (uiState.messageInput.isNotBlank()) onSendMessageClicked() }
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onSendMessageClicked,
                        enabled = uiState.messageInput.isNotBlank() && !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }

            // Tratamento do Estado de LOADING
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Tratamento do Estado de ERROR
            if (uiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    action = {
                        TextButton(onClick = onErrorDismissed) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(uiState.errorMessage)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    // Nota: A identificação se a mensagem é do usuário atual será tratada reativamente no ViewModel pela Pessoa 2
    val isCurrentUser = false

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isCurrentUser) Color(0xFF2196F3) else Color(0xFFF5F5F5)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) Color.White else Color.Gray
                )
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentUser) Color.White else Color.Black
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrentUser) Color.White else Color.Gray
                )
            }
        }
    }
}
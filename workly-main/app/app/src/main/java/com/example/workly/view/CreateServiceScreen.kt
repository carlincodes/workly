package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workly.presentation.service.CreateServiceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
    navController: NavController,
    uiState: CreateServiceUiState, // Estado centralizado que controla a tela
    onTitleChanged: (String) -> Unit, // Mudança no título
    onDescriptionChanged: (String) -> Unit, // Mudança na descrição
    onJobTypeSelected: (String) -> Unit, // Seleção no dropdown
    onDropdownToggled: (Boolean) -> Unit, // Abrir/fechar dropdown
    onSaveClicked: () -> Unit // Clicar no botão Salvar
) {
    val jobTypes = listOf(
        "Eletricista",
        "Encanador",
        "Pintor",
        "Pedreiro",
        "Marido de aluguel",
        "Limpeza",
        "Outro"
    )

    // Se o ViewModel processar o sucesso e marcar como criado, a tela apenas volta
    if (uiState.isServiceCreated) {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Criar Serviço",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Campo de Título consome do estado e avisa a mudança para fora
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChanged,
            label = { Text("Título do serviço") },
            placeholder = { Text("Ex: Troca de chuveiro") },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Dropdown Menu para Tipo de Profissional
        ExposedDropdownMenuBox(
            expanded = uiState.isJobTypeExpanded,
            onExpandedChange = onDropdownToggled,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = uiState.selectedJobType,
                onValueChange = {},
                label = { Text("Tipo de profissional") },
                modifier = Modifier.menuAnchor(),
                enabled = !uiState.isLoading
            )
            ExposedDropdownMenu(
                expanded = uiState.isJobTypeExpanded,
                onDismissRequest = { onDropdownToggled(false) }
            ) {
                jobTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = { onJobTypeSelected(type) }
                    )
                }
            }
        }

        // Campo de Descrição consome do estado e avisa a mudança para fora
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChanged,
            label = { Text("Descrição") },
            placeholder = { Text("Detalhes do serviço, local, urgência...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            enabled = !uiState.isLoading
        )

        // Exibição de mensagem de erro vinda do estado (caso a validação do ViewModel falhe)
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                enabled = !uiState.isLoading
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSaveClicked,
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Salvar")
                }
            }
        }
    }
}
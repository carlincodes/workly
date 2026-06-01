package com.example.workly.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.DropdownMenuItem
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.workly.model.Service
import com.example.workly.network.RetrofitClient
import com.example.workly.repository.ServiceRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(navController: NavController, serviceId: String? = null) {

    // 1. Estados da tela organizados no topo
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cep by remember { mutableStateOf("") }
    var addressInfo by remember { mutableStateOf("") }

    var jobTypeExpanded by remember { mutableStateOf(false) }
    var selectedJobType by remember { mutableStateOf("Eletricista") }
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val repository = remember { ServiceRepository() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // 2. Carrega os dados de forma totalmente segura contra crash
    LaunchedEffect(serviceId) {
        if (!serviceId.isNullOrEmpty()) {
            try {
                // Busca de forma isolada para evitar sobrecarga na thread principal
                val existingService = repository.getServiceById(serviceId)
                if (existingService != null) {
                    title = existingService.title.orEmpty()
                    description = existingService.description.orEmpty()
                    selectedJobType = if (existingService.jobType.isNotEmpty()) existingService.jobType else "Eletricista"
                }
            } catch (e: Exception) {
                // Evita que o app feche se houver erro de conversão de dados do Firebase
                Toast.makeText(context, "Aviso: Carregando dados de formulário limpo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val jobTypes = listOf(
        "Eletricista",
        "Encanador",
        "Pintor",
        "Pedreiro",
        "Marido de aluguel",
        "Limpeza",
        "Outro"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (!serviceId.isNullOrEmpty()) "Editar Serviço" else "Criar Serviço") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título do serviço") },
                placeholder = { Text("Ex: Troca de chuveiro") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = jobTypeExpanded,
                onExpandedChange = { jobTypeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedJobType,
                    onValueChange = {},
                    label = { Text("Tipo de profissional") },
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jobTypeExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = jobTypeExpanded,
                    onDismissRequest = { jobTypeExpanded = false }
                ) {
                    jobTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedJobType = type
                                jobTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // Consumo da API REST do ViaCEP com Retrofit (Requisito de PDM)
            OutlinedTextField(
                value = cep,
                onValueChange = { cep = it },
                label = { Text("CEP (Opcional)") },
                placeholder = { Text("Ex: 01001000") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            try {
                                val limpandoCep = cep.replace("-", "").trim()
                                if (limpandoCep.length == 8) {
                                    val response = RetrofitClient.viaCepService.getAddress(limpandoCep)
                                    addressInfo = "${response.logradouro}, ${response.bairro}, ${response.localidade}-${response.uf}"
                                    Toast.makeText(context, "Endereço localizado!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Insira um CEP válido com 8 dígitos", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao buscar CEP", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar CEP")
                    }
                }
            )

            if (addressInfo.isNotEmpty()) {
                Text(
                    text = addressInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                placeholder = { Text("Detalhes do serviço, local, urgência...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (!isSaving) {
                            isSaving = true
                            scope.launch {
                                try {
                                    val finalId = serviceId ?: ""

                                    val fullDescription = if (addressInfo.isNotEmpty() && !description.contains("Local:"))
                                        "$description \nLocal: $addressInfo"
                                    else description

                                    val serviceData = Service(
                                        id = finalId,
                                        title = title,
                                        description = fullDescription,
                                        jobType = selectedJobType,
                                        clientId = "user_anonimo"
                                    )

                                    if (!serviceId.isNullOrEmpty()) {
                                        repository.updateService(serviceData)
                                    } else {
                                        repository.createService(serviceData)
                                    }

                                    Toast.makeText(context, "Serviço salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    },
                    enabled = title.isNotBlank() && description.isNotBlank() && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun CreateServiceScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        CreateServiceScreen(navController = navController)
    }
}
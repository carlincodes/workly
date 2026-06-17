package com.example.workly.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignupScreen(navController: NavController) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var selectedRole by remember {
        mutableStateOf("client")
    }

    val context = LocalContext.current

    val isInPreview = LocalInspectionMode.current

    val auth =
        if (!isInPreview)
            FirebaseAuth.getInstance()
        else
            null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
            .padding(bottom = 110.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Workly",

            style = MaterialTheme
                .typography
                .headlineLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Crie sua conta",

            style = MaterialTheme
                .typography
                .bodyMedium,

            color = MaterialTheme
                .colorScheme
                .onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            placeholder = {
                Text("seu@email.com")
            },

            leadingIcon = {
                Icon(Icons.Default.Email, null)
            },

            singleLine = true,

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Senha")
            },

            placeholder = {
                Text("Digite sua senha")
            },

            leadingIcon = {
                Icon(Icons.Default.Lock, null)
            },

            singleLine = true,

            modifier = Modifier.fillMaxWidth(),

            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {

                val image =
                    if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                val description =
                    if (passwordVisible)
                        "Ocultar senha"
                    else
                        "Mostrar senha"

                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(image, description)
                }
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,

            onValueChange = {
                confirmPassword = it
            },

            label = {
                Text("Confirmar senha")
            },

            placeholder = {
                Text("Digite novamente")
            },

            leadingIcon = {
                Icon(Icons.Default.Lock, null)
            },

            singleLine = true,

            modifier = Modifier.fillMaxWidth(),

            visualTransformation =
                if (confirmPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {

                val image =
                    if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                val description =
                    if (confirmPasswordVisible)
                        "Ocultar senha"
                    else
                        "Mostrar senha"

                IconButton(
                    onClick = {
                        confirmPasswordVisible =
                            !confirmPasswordVisible
                    }
                ) {
                    Icon(image, description)
                }
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedRole == "client",
                onClick = { selectedRole = "client" },
                label = { Text("Cliente") }
            )
            FilterChip(
                selected = selectedRole == "provider",
                onClick = { selectedRole = "provider" },
                label = { Text("Prestador") }
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(

            onClick = {

                if (
                    email.isBlank() ||
                    password.isBlank() ||
                    confirmPassword.isBlank()
                ) {

                    Toast.makeText(
                        context,
                        "Preencha todos os campos",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                if (password != confirmPassword) {

                    Toast.makeText(
                        context,
                        "As senhas não coincidem",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                auth
                    ?.createUserWithEmailAndPassword(
                        email,
                        password
                    )

                    ?.addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            Toast.makeText(
                                context,
                                "Conta criada com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()

                            navController.navigate(
                                if (selectedRole == "provider") "provider_home" else "client_home"
                            )

                        } else {

                            Toast.makeText(
                                context,

                                task.exception?.message
                                    ?: "Erro ao cadastrar",

                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)

        ) {

            Text("Cadastrar")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        TextButton(

            onClick = {

                navController.navigate(
                    "login"
                )
            }

        ) {

            Text(
                "Já tem conta? Entrar"
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 800
)

@Composable
fun SignupScreenPreview() {

    val navController =
        rememberNavController()

    MaterialTheme {

        SignupScreen(
            navController = navController
        )
    }
}
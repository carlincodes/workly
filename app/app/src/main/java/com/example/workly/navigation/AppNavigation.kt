package com.example.workly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workly.view.ChatScreen
import com.example.workly.view.ClientHomeScreen
import com.example.workly.view.ClientProfileScreen
import com.example.workly.view.CreateServiceScreen
import com.example.workly.view.LoginScreen
import com.example.workly.view.MapScreen
import com.example.workly.view.ProfileScreen
import com.example.workly.view.ProviderHomeScreen
import com.example.workly.view.SignupScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("signup") {
            SignupScreen(navController)
        }

        composable("client_home") {
            ClientHomeScreen(navController)
        }

        composable("provider_home") {
            ProviderHomeScreen(navController)
        }

        composable(
            route = "create_service?serviceId={serviceId}",
            arguments = listOf(navArgument("serviceId") {
                nullable = true
                defaultValue = null
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            CreateServiceScreen(navController = navController, serviceId = serviceId)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("client_profile") {
            ClientProfileScreen(navController)
        }

        composable("map") {
            MapScreen(navController)
        }

        composable(
            "chat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(navController, chatId)
        }
    }
}
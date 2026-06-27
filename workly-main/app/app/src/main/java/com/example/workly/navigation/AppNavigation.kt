package com.example.workly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workly.view.ApiServicesScreen
import com.example.workly.view.ChatScreen
import com.example.workly.view.ClientHomeScreen
import com.example.workly.view.ClientProfileScreen
import com.example.workly.view.CreateServiceScreen
import com.example.workly.view.LoginScreen
import com.example.workly.view.MapScreen
import com.example.workly.view.ProfileScreen
import com.example.workly.view.ProviderHomeScreen
import com.example.workly.view.ServiceManagementScreen
import com.example.workly.view.ServiceDetailScreen
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

        composable("api_services") {
            ApiServicesScreen(navController)
        }

        composable("service_management") {
            ServiceManagementScreen(navController)
        }

        composable("service_detail/{role}/{category}/{title}?description={description}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role")
            val category = backStackEntry.arguments?.getString("category")
            val title = backStackEntry.arguments?.getString("title")
            val description = backStackEntry.arguments?.getString("description")

            ServiceDetailScreen(
                navController = navController,
                role = role,
                category = category,
                title = title,
                description = description
            )
        }

        composable("create_service") {
            CreateServiceScreen(navController)
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

        composable("chat") {
            ChatScreen(navController)
        }
    }
}
package com.example.workly.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import org.koin.androidx.compose.koinViewModel

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

import com.example.workly.viewmodel.AuthViewModel
import com.example.workly.viewmodel.ChatViewModel
import com.example.workly.viewmodel.HomeViewModel
import com.example.workly.viewmodel.MapViewModel
import com.example.workly.viewmodel.ProfileViewModel
import com.example.workly.viewmodel.ServiceViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            val authViewModel: AuthViewModel = koinViewModel()
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    navController.navigate(uiState.destinationRoute ?: "client_home") {
                        popUpTo("login") { inclusive = true }
                    }
                    authViewModel.clearSuccess()
                }
            }

            LoginScreen(
                navController = navController,
                uiState = uiState,
                onEmailChanged = authViewModel::onEmailChanged,
                onPasswordChanged = authViewModel::onPasswordChanged,
                onTogglePasswordVisibility = authViewModel::onTogglePasswordVisibility,
                onLoginClicked = authViewModel::onLoginClicked
            )
        }

        composable("signup") {
            val authViewModel: AuthViewModel = koinViewModel()
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    navController.popBackStack()
                    authViewModel.clearSuccess()
                }
            }

            SignupScreen(
                navController = navController,
                uiState = uiState,
                onEmailChanged = authViewModel::onEmailChanged,
                onPasswordChanged = authViewModel::onPasswordChanged,
                onConfirmPasswordChanged = authViewModel::onConfirmPasswordChanged,
                onTogglePasswordVisibility = authViewModel::onTogglePasswordVisibility,
                onSignupClicked = authViewModel::onSignupClicked
            )
        }

        composable("client_home") {
            val homeViewModel: HomeViewModel = koinViewModel()
            val uiState by homeViewModel.clientUiState.collectAsState()

            LaunchedEffect(Unit) {
                homeViewModel.loadClientServices()
            }

            ClientHomeScreen(
                navController = navController,
                uiState = uiState,
                onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
                onCategorySelected = homeViewModel::onCategorySelected
            )
        }

        composable("provider_home") {
            val homeViewModel: HomeViewModel = koinViewModel()
            val uiState by homeViewModel.providerUiState.collectAsState()

            LaunchedEffect(Unit) {
                homeViewModel.loadProviderServices()
            }

            ProviderHomeScreen(
                navController = navController,
                uiState = uiState,
                onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
                onCategorySelected = homeViewModel::onCategorySelected
            )
        }

        composable("api_services") {
            val serviceViewModel: ServiceViewModel = koinViewModel()
            val uiState by serviceViewModel.apiUiState.collectAsState()

            ApiServicesScreen(
                navController = navController,
                uiState = uiState,
                onCepChanged = serviceViewModel::onCepChanged,
                onSearchClicked = serviceViewModel::onSearchClicked
            )
        }

        composable("service_management") {
            val serviceViewModel: ServiceViewModel = koinViewModel()
            val uiState by serviceViewModel.managementUiState.collectAsState()

            LaunchedEffect(Unit) {
                serviceViewModel.loadUserServices()
            }

            ServiceManagementScreen(
                navController = navController,
                uiState = uiState,
                onEditClicked = serviceViewModel::onEditClicked,
                onDeleteClicked = serviceViewModel::onDeleteClicked,
                onEditTitleChanged = serviceViewModel::onEditTitleChanged,
                onEditDescriptionChanged = serviceViewModel::onEditDescriptionChanged,
                onEditCategoryChanged = serviceViewModel::onEditCategoryChanged,
                onSaveEditClicked = serviceViewModel::onSaveEditClicked,
                onDismissDialog = serviceViewModel::onDismissDialog
            )
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
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry("service_management")
            }
            val serviceViewModel: ServiceViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            val uiState by serviceViewModel.createServiceUiState.collectAsState()

            LaunchedEffect(uiState.isServiceCreated) {
                if (uiState.isServiceCreated) {
                    navController.popBackStack()
                    serviceViewModel.resetCreateServiceState()
                }
            }

            CreateServiceScreen(
                navController = navController,
                uiState = uiState,
                onTitleChanged = serviceViewModel::onTitleChanged,
                onDescriptionChanged = serviceViewModel::onDescriptionChanged,
                onJobTypeSelected = serviceViewModel::onJobTypeSelected,
                onDropdownToggled = serviceViewModel::onDropdownToggled,
                onSaveClicked = serviceViewModel::onSaveClicked
            )
        }

        composable("profile") {
            val profileViewModel: ProfileViewModel = koinViewModel()
            val uiState by profileViewModel.uiState.collectAsState()

            ProfileScreen(
                navController = navController,
                uiState = uiState,
                onProfileDataChanged = profileViewModel::onProfileDataChanged,
                onSaveClicked = { profileViewModel.saveProfile(uiState.profileData) },
                onLogoutClicked = { navController.navigate("login") }
            )
        }

        composable("client_profile") {
            val profileViewModel: ProfileViewModel = koinViewModel()
            val uiState by profileViewModel.uiState.collectAsState()

            ClientProfileScreen(
                navController = navController,
                uiState = uiState,
                onProfileDataChanged = profileViewModel::onProfileDataChanged,
                onSaveClicked = { profileViewModel.saveProfile(uiState.profileData) },
                onLogoutClicked = { navController.navigate("login") }
            )
        }

        composable("map") {
            val mapViewModel: MapViewModel = koinViewModel()
            val uiState by mapViewModel.uiState.collectAsState()

            MapScreen(
                navController = navController,
                uiState = uiState,
                onRadiusChanged = mapViewModel::onRadiusChanged,
                onProviderClicked = mapViewModel::onProviderClicked
            )
        }

        composable("chat") {
            val chatViewModel: ChatViewModel = koinViewModel()
            val uiState by chatViewModel.uiState.collectAsState()

            ChatScreen(
                navController = navController,
                uiState = uiState,
                onMessageTextChanged = chatViewModel::onMessageTextChanged,
                onSendMessageClicked = chatViewModel::onSendMessageClicked,
                onErrorDismissed = chatViewModel::onErrorDismissed
            )
        }
    }
}
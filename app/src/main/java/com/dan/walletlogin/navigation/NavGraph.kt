package com.dan.walletlogin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dan.walletlogin.AuthState
import com.dan.walletlogin.AuthViewModel
import com.dan.walletlogin.LoginScreen
import com.dan.walletlogin.ui.screens.*

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Observar el estado de autenticación para redirigir
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthState.Success -> {
                // Redirigir a Home y limpiar backstack del login
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.Idle -> {
                // Si el estado vuelve a Idle (logout), ir a login
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(authViewModel)
        }
        
        composable("home") {
            val email = (uiState as? AuthState.Success)?.email ?: ""
            HomeScreen(
                email = email,
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        
        composable("buscar") {
            BuscarScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        
        composable("mis_viajes") {
            MisViajesScreen(
                viajes = emptyList(), // TODO: conectar con Firestore
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        
        composable("perfil") {
            val email = (uiState as? AuthState.Success)?.email ?: ""
            PerfilScreen(
                nombre = email.split("@").firstOrNull() ?: "Usuario",
                email = email,
                onNavigate = { route -> navController.navigate(route) },
                onCerrarSesion = { authViewModel.logout(context) }
            )
        }
    }
}

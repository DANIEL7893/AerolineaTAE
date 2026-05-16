package com.dan.aerolineaTAE.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dan.aerolineaTAE.AuthState
import com.dan.aerolineaTAE.AuthViewModel
import com.dan.aerolineaTAE.LoginScreen
import com.dan.aerolineaTAE.MfaViewModel
import com.dan.aerolineaTAE.ui.screens.*

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    mfaViewModel: MfaViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Determinamos la pantalla inicial solo una vez al arrancar
    val startDestination = remember {
        if (authViewModel.uiState.value is AuthState.Success) "home" else "login"
    }

    // Observar el estado de autenticación para redirigir
    LaunchedEffect(uiState) {
        val currentState = uiState
        when (currentState) {
            is AuthState.Success -> {
                // Si el usuario se loguea (viene de login), lo mandamos a home
                if (navController.currentDestination?.route == "login" || 
                    navController.currentDestination?.route == "sms_code") {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is AuthState.Idle -> {
                // Si cierra sesión, lo mandamos a login
                if (navController.currentDestination?.route != "login") {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.RequiresMfa -> {
                mfaViewModel.setResolver(currentState.resolver)
                navController.navigate("sms_code")
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(authViewModel)
        }

        composable("sms_code") {
            SmsCodeScreen(
                mfaViewModel = mfaViewModel,
                onSuccess = { user ->
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("enroll_mfa") {
            EnrollPhoneScreen(
                mfaViewModel = mfaViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
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
        
        composable("check-in") {
            CheckInScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable("vuelos_disponibles") {
            VuelosDisponiblesScreen(
                onBack = { navController.popBackStack() }
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

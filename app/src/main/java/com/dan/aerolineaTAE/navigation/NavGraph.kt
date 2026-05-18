package com.dan.aerolineaTAE.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dan.aerolineaTAE.AuthState
import com.dan.aerolineaTAE.AuthViewModel
import com.dan.aerolineaTAE.LoginScreen
import com.dan.aerolineaTAE.data.Reserva
import com.dan.aerolineaTAE.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val startDestination = remember {
        val state = authViewModel.uiState.value
        when (state) {
            is AuthState.Success -> "home"
            is AuthState.RequiresOnboarding -> "onboarding"
            is AuthState.RequiresMfa -> "mfa_challenge"
            else -> "login"
        }
    }

    LaunchedEffect(uiState) {
        val currentState = uiState
        when (currentState) {
            is AuthState.Success -> {
                if (navController.currentDestination?.route in listOf("login", "mfa_challenge", "onboarding", "datos_personales")) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is AuthState.Idle -> {
                if (navController.currentDestination?.route != "login") {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthState.RequiresMfa -> {
                if (navController.currentDestination?.route != "mfa_challenge") {
                    navController.navigate("mfa_challenge") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is AuthState.RequiresOnboarding -> {
                if (navController.currentDestination?.route != "onboarding") {
                    navController.navigate("onboarding") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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

        composable("mfa_challenge") {
            MfaChallengeScreen(authViewModel)
        }

        composable("onboarding") {
            DatosPersonalesScreen(
                authViewModel = authViewModel,
                onBack = { 
                    val email = (authViewModel.uiState.value as? AuthState.RequiresOnboarding)?.email ?: ""
                    authViewModel.completeOnboarding(email)
                }
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

        composable(
            route = "vuelos_disponibles/{destinoId}",
            arguments = listOf(navArgument("destinoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val destinoId = backStackEntry.arguments?.getString("destinoId") ?: ""
            VuelosDisponiblesScreen(
                destinoId = destinoId,
                onBack = { navController.popBackStack() },
                onComprar = { vuelo ->
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val db = FirebaseFirestore.getInstance()
                        val pnr = (1..6).map { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")
                        val nuevaReserva = Reserva(
                            userId = user.uid,
                            vueloId = vuelo.id,
                            pnr = pnr,
                            origen = vuelo.origen,
                            destino = vuelo.destino,
                            fecha = vuelo.fecha,
                            horaSalida = vuelo.horaSalida,
                            precio = vuelo.precio,
                            fechaCompra = System.currentTimeMillis()
                        )
                        db.collection("reservas").add(nuevaReserva).addOnSuccessListener {
                            navController.navigate("mis_viajes") {
                                popUpTo("home")
                            }
                        }
                    }
                }
            )
        }
        
        composable("mis_viajes") {
            MisViajesScreen(
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

        composable("datos_personales") {
            VerPerfilScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "boarding_pass/{pnr}",
            arguments = listOf(navArgument("pnr") { type = NavType.StringType })
        ) { backStackEntry ->
            val pnr = backStackEntry.arguments?.getString("pnr") ?: ""
            BoardingPassScreen(
                pnr = pnr,
                onBack = { 
                    navController.navigate("mis_viajes") {
                        popUpTo("home")
                    }
                }
            )
        }

        composable("ayuda") {
            AyudaScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("mfa_settings") {
            MfaSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

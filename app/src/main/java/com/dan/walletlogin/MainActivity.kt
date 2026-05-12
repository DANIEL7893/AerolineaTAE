package com.dan.walletlogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dan.walletlogin.navigation.NavGraph
import com.dan.walletlogin.ui.theme.TaeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Aplicamos el tema personalizado de Aerolíneas TAE
            TaeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val authViewModel: AuthViewModel = viewModel()
                    // Reemplazamos LoginScreen por el NavGraph para manejar toda la navegación
                    NavGraph(authViewModel)
                }
            }
        }
    }
}

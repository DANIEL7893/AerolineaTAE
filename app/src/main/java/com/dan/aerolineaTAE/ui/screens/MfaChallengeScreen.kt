package com.dan.aerolineaTAE.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.dan.aerolineaTAE.AuthState
import com.dan.aerolineaTAE.AuthViewModel
import com.dan.aerolineaTAE.ui.theme.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun MfaChallengeScreen(
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    
    val mfaData = uiState as? AuthState.RequiresMfa
    val email = mfaData?.email ?: ""
    val mfaType = mfaData?.type ?: "pin"
    val pinCorrecto = mfaData?.pinCorrecto ?: ""

    var pinIngresado by remember { mutableStateOf("") }
    
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    fun launchBiometric() {
        val activity = context as? FragmentActivity ?: return
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                authViewModel.verifyMfa(true, email)
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Usa tu huella para acceder a Aerolíneas TAE")
            .setNegativeButtonText("Usar PIN alternativo")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(mfaType) {
        if (mfaType == "biometric") {
            launchBiometric()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulMuyClaro)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (mfaType == "biometric") Icons.Default.Fingerprint else Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = AzulPrincipal
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Verificación Requerida",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AzulOscuro
        )
        
        Text(
            text = if (mfaType == "biometric") "Usa tu huella dactilar para continuar." 
                   else "Ingresa tu PIN de seguridad de 6 dígitos.",
            fontSize = 14.sp,
            color = GrisTexto,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (mfaType == "pin") {
            OutlinedTextField(
                value = pinIngresado,
                onValueChange = { if (it.length <= 6) pinIngresado = it },
                label = { Text("PIN") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (pinIngresado == pinCorrecto) authViewModel.verifyMfa(true, email)
                    else authViewModel.verifyMfa(false, email)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                enabled = pinIngresado.length == 6
            ) {
                Text("Verificar y Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { launchBiometric() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
            ) {
                Text("Reintentar Huella", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (uiState is AuthState.Error) {
            Text(
                text = (uiState as AuthState.Error).message,
                color = RojoError,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        
        TextButton(
            onClick = { authViewModel.logout(context) }, 
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Cerrar sesión", color = GrisTexto)
        }
    }
}

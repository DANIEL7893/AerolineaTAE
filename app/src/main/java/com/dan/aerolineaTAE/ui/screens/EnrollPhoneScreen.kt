package com.dan.aerolineaTAE.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.MfaState
import com.dan.aerolineaTAE.MfaViewModel
import com.dan.aerolineaTAE.ui.theme.AzulMuyClaro
import com.dan.aerolineaTAE.ui.theme.AzulPrincipal
import com.dan.aerolineaTAE.ui.theme.Blanco
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollPhoneScreen(
    mfaViewModel: MfaViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by mfaViewModel.uiState.collectAsState()
    val verificationId by mfaViewModel.verificationId.collectAsState()
    
    var phoneNumber by remember { mutableStateOf("+503") }
    var smsCode by remember { mutableStateOf("") }
    
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    LaunchedEffect(uiState) {
        if (uiState is MfaState.Success) {
            Toast.makeText(context, "MFA Activado correctamente", Toast.LENGTH_SHORT).show()
            onSuccess()
            mfaViewModel.reset()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activar MFA", color = Blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Seguridad de la cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Registra tu teléfono para recibir códigos de verificación por SMS.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de teléfono (ej. +503...)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = verificationId == null
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (verificationId == null) {
                Button(
                    onClick = {
                        if (phoneNumber.length > 8) {
                            mfaViewModel.setLoading()
                            
                            val options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(context as Activity)
                                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                                        // Auto-verificación (opcional)
                                    }

                                    override fun onVerificationFailed(e: FirebaseException) {
                                        mfaViewModel.setError(e.message ?: "Error al enviar SMS")
                                    }

                                    override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                                        mfaViewModel.setVerificationInfo(id, token)
                                        mfaViewModel.reset() // Volver a Idle pero con ID guardado
                                        Toast.makeText(context, "Código enviado", Toast.LENGTH_SHORT).show()
                                    }
                                })
                                .build()
                            
                            PhoneAuthProvider.verifyPhoneNumber(options)
                        } else {
                            Toast.makeText(context, "Número inválido", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                    enabled = uiState !is MfaState.Loading
                ) {
                    if (uiState is MfaState.Loading) {
                        CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Enviar SMS de verificación")
                    }
                }
            } else {
            OutlinedTextField(
                value = smsCode,
                onValueChange = { if (it.length <= 6) smsCode = it },
                label = { Text("Código de 6 dígitos") },
                // TODO: [Ingresar el código de 6 dígitos recibido por SMS para completar el registro del segundo factor]
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (smsCode.length == 6) {
                            mfaViewModel.setLoading()
                            val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
                            val assertion = PhoneMultiFactorGenerator.getAssertion(credential)
                            
                            user?.multiFactor?.enroll(assertion, "Mi Telefono")
                                ?.addOnSuccessListener {
                                    mfaViewModel.setSuccess()
                                }
                                ?.addOnFailureListener {
                                    mfaViewModel.setError(it.message ?: "Error al enrolar")
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                    enabled = uiState !is MfaState.Loading && smsCode.length == 6
                ) {
                    if (uiState is MfaState.Loading) {
                        CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Activar Multifactor")
                    }
                }
                
                TextButton(onClick = { mfaViewModel.reset() }) {
                    Text("Cambiar número")
                }
            }

            if (uiState is MfaState.Error) {
                Text(
                    text = (uiState as MfaState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

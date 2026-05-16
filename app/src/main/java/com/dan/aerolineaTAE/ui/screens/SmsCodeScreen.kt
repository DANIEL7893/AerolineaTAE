package com.dan.aerolineaTAE.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

@Composable
fun SmsCodeScreen(
    mfaViewModel: MfaViewModel,
    onSuccess: (FirebaseUser) -> Unit
) {
    val context = LocalContext.current
    val uiState by mfaViewModel.uiState.collectAsState()
    val resolver by mfaViewModel.resolver.collectAsState()
    val verificationId by mfaViewModel.verificationId.collectAsState()
    
    var smsCode by remember { mutableStateOf("") }
    
    val auth = FirebaseAuth.getInstance()

    // Enviar SMS automáticamente al cargar si hay resolver
    LaunchedEffect(resolver) {
        if (resolver != null && verificationId == null) {
            val phoneHint = resolver!!.hints[0] as PhoneMultiFactorInfo
            
            val options = PhoneAuthOptions.newBuilder(auth)
                .setMultiFactorHint(phoneHint)
                .setMultiFactorSession(resolver!!.session)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
                    override fun onVerificationFailed(e: FirebaseException) {
                        mfaViewModel.setError(e.message ?: "Error al enviar SMS")
                    }
                    override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                        mfaViewModel.setVerificationInfo(id, token)
                        Toast.makeText(context, "Código enviado", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
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
        Text(
            text = "Verificación de dos pasos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AzulPrincipal
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Se ha enviado un código de seguridad a tu teléfono registrado.",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = smsCode,
            onValueChange = { if (it.length <= 6) smsCode = it },
            label = { Text("Código de 6 dígitos") },
            // TODO: [El código de 6 dígitos llega por SMS al teléfono del usuario registrado en Firebase MFA]
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (smsCode.length == 6 && resolver != null && verificationId != null) {
                    mfaViewModel.setLoading()
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
                    val assertion = PhoneMultiFactorGenerator.getAssertion(credential)
                    
                    resolver!!.resolveSignIn(assertion)
                        .addOnSuccessListener { result ->
                            mfaViewModel.setSuccess()
                            onSuccess(result.user!!)
                        }
                        .addOnFailureListener {
                            mfaViewModel.setError(it.message ?: "Código inválido")
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
            enabled = uiState !is MfaState.Loading && smsCode.length == 6
        ) {
            if (uiState is MfaState.Loading) {
                CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
            } else {
                Text("Verificar y Acceder", fontWeight = FontWeight.Bold)
            }
        }

        if (uiState is MfaState.Error) {
            Text(
                text = (uiState as MfaState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

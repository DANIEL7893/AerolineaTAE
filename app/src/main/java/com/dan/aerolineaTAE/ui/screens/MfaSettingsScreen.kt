package com.dan.aerolineaTAE.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Usuario
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MfaSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }

    var mfaEnabled by remember { mutableStateOf(false) }
    var mfaType by remember { mutableStateOf("none") }
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    usuario = document.toObject(Usuario::class.java)
                    usuario?.let {
                        mfaEnabled = it.mfaEnabled
                        mfaType = it.mfaType
                        pin = it.pin
                    }
                    cargando = false
                }
                .addOnFailureListener { cargando = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguridad (MFA)", color = Blanco, fontWeight = FontWeight.Bold) },
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
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AzulPrincipal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Protección de cuenta",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Doble factor de autenticación", fontWeight = FontWeight.Bold)
                            Text("Añade una capa extra de seguridad al iniciar sesión.", fontSize = 12.sp, color = GrisTexto)
                        }
                        Switch(
                            checked = mfaEnabled,
                            onCheckedChange = { 
                                mfaEnabled = it
                                if (!it) mfaType = "none"
                                else if (mfaType == "none") mfaType = "pin"
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = AzulPrincipal)
                        )
                    }
                }

                if (mfaEnabled) {
                    Text("Selecciona un método:", fontWeight = FontWeight.Medium, color = AzulOscuro)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MethodCard(
                            title = "PIN",
                            icon = Icons.Default.Pin,
                            selected = mfaType == "pin",
                            onClick = { mfaType = "pin" },
                            modifier = Modifier.weight(1f)
                        )
                        MethodCard(
                            title = "Huella",
                            icon = Icons.Default.Fingerprint,
                            selected = mfaType == "biometric",
                            onClick = { mfaType = "biometric" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (mfaType == "pin") {
                        OutlinedTextField(
                            value = pin,
                            onValueChange = { if (it.length <= 6) pin = it },
                            label = { Text("PIN de 6 dígitos") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (mfaEnabled && mfaType == "pin" && pin.length < 6) {
                            Toast.makeText(context, "El PIN debe ser de 6 dígitos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        guardando = true
                        val updates = mapOf(
                            "mfaEnabled" to mfaEnabled,
                            "mfaType" to mfaType,
                            "pin" to if (mfaType == "pin") pin else ""
                        )
                        db.collection("users").document(userId).update(updates)
                            .addOnSuccessListener {
                                guardando = false
                                Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                            .addOnFailureListener {
                                guardando = false
                                Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                    enabled = !guardando
                ) {
                    if (guardando) CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                    else Text("Guardar configuración", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AzulPrincipal else Blanco,
            contentColor = if (selected) Blanco else AzulOscuro
        ),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, AzulMuyClaro) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold)
        }
    }
}

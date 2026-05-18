package com.dan.aerolineaTAE.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.AuthState
import com.dan.aerolineaTAE.data.Usuario
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatosPersonalesScreen(
    authViewModel: com.dan.aerolineaTAE.AuthViewModel? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val userId = user?.uid ?: ""

    var esOnboarding by remember { mutableStateOf(false) }
    
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var countryCode by remember { mutableStateOf("+503") }
    var phoneNumber by remember { mutableStateOf("") }
    
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.toObject(Usuario::class.java)
                        userData?.let {
                            nombre = it.nombre
                            apellido = it.apellido
                            if (it.email.isNotEmpty()) email = it.email
                            if (it.telefono.startsWith("+")) {
                                countryCode = it.telefono.take(4)
                                phoneNumber = it.telefono.drop(4)
                            } else {
                                phoneNumber = it.telefono
                            }
                        }
                        esOnboarding = false
                    } else {
                        esOnboarding = true
                    }
                    cargando = false
                }
                .addOnFailureListener { cargando = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esOnboarding) "Completa tu perfil" else "Datos personales", color = Blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (!esOnboarding) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Blanco)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AzulPrincipal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (esOnboarding) "Bienvenido a TAE" else "Información de cuenta",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = apellido, onValueChange = { apellido = it },
                    label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = countryCode, onValueChange = { countryCode = it },
                        label = { Text("Cod.") }, modifier = Modifier.width(90.dp),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = phoneNumber, onValueChange = { phoneNumber = it },
                        label = { Text("Teléfono") }, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() || apellido.isBlank() || phoneNumber.isBlank()) {
                            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        guardando = true
                        val fullPhone = countryCode.trim() + phoneNumber.trim()
                        
                        val updates = mapOf(
                            "nombre" to nombre,
                            "apellido" to apellido,
                            "email" to email,
                            "telefono" to fullPhone
                        )
                        
                        db.collection("users").document(userId).set(updates, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener {
                                guardando = false
                                Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                                if (esOnboarding && authViewModel != null) {
                                    authViewModel.completeOnboarding(email)
                                }
                                onBack()
                            }
                            .addOnFailureListener {
                                guardando = false
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                    enabled = !guardando
                ) {
                    if (guardando) CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                    else Text(if (esOnboarding) "Activar Cuenta" else "Guardar Cambios", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Usuario
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerPerfilScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    usuario = document.toObject(Usuario::class.java)
                    cargando = false
                }
                .addOnFailureListener { cargando = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Datos", color = Blanco, fontWeight = FontWeight.Bold) },
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AzulPrincipal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Información Personal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )

                usuario?.let { user ->
                    InfoCard(icon = Icons.Default.Person, label = "Nombre Completo", value = "${user.nombre} ${user.apellido}")
                    InfoCard(icon = Icons.Default.Email, label = "Correo Electrónico", value = user.email)
                    InfoCard(icon = Icons.Default.Phone, label = "Teléfono", value = user.telefono)
                    
                    val mfaTexto = if (user.mfaEnabled) "Activado (${if (user.mfaType == "pin") "PIN" else "Huella"})" else "Desactivado"
                    InfoCard(icon = Icons.Default.Security, label = "Doble Factor (MFA)", value = mfaTexto)
                }
            }
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AzulClaro, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, fontSize = 12.sp, color = GrisTexto)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            }
        }
    }
}

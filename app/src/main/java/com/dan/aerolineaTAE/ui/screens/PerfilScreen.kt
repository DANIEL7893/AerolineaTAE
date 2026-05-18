package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    nombre: String,
    email: String,
    onNavigate: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    
    var nombreUsuario by remember { mutableStateOf(nombre) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val realName = document.getString("nombre")
                    if (!realName.isNullOrBlank()) {
                        nombreUsuario = realName
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = Blanco, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        bottomBar = {
            TaeBottomNavBar(
                currentRoute = "perfil",
                onNavigate = onNavigate
            )
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Blanco)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = AzulClaro
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (nombreUsuario.isNotEmpty()) nombreUsuario.take(1).uppercase() else "U",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Blanco
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = nombreUsuario, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                Text(text = email, fontSize = 14.sp, color = GrisTexto)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column {
                    PerfilMenuItem(icon = Icons.Default.Person, title = "Datos personales") {
                        onNavigate("datos_personales")
                    }
                    HorizontalDivider(color = AzulMuyClaro, modifier = Modifier.padding(horizontal = 16.dp))
                    PerfilMenuItem(icon = Icons.Default.Security, title = "Seguridad (MFA)") {
                        onNavigate("mfa_settings")
                    }
                    HorizontalDivider(color = AzulMuyClaro, modifier = Modifier.padding(horizontal = 16.dp))
                    PerfilMenuItem(icon = Icons.Default.Help, title = "Ayuda") {
                        onNavigate("ayuda")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                PerfilMenuItem(
                    icon = Icons.Default.Logout,
                    title = "Cerrar sesión",
                    textColor = RojoError,
                    iconColor = RojoError,
                    onClick = onCerrarSesion
                )
            }
        }
    }
}

@Composable
fun PerfilMenuItem(
    icon: ImageVector,
    title: String,
    textColor: Color = AzulOscuro,
    iconColor: Color = AzulClaro,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp, color = textColor)
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = GrisTexto)
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    TaeTheme {
        PerfilScreen(
            nombre = "Daniel",
            email = "Daniel@example.com",
            onNavigate = {},
            onCerrarSesion = {}
        )
    }
}

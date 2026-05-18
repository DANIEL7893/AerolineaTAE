package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyudaScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Centro de Ayuda", color = Blanco, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.HelpCenter,
                contentDescription = null,
                tint = AzulClaro,
                modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally)
            )
            
            Text(
                "¿Cómo podemos ayudarte?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AyudaItem(
                "¿Cómo comprar un vuelo?",
                "Ve a la sección 'Reservar', selecciona tu destino y fechas. En la lista de resultados, elige el vuelo que prefieras y presiona 'Comprar'."
            )

            AyudaItem(
                "¿Qué es el código PNR?",
                "Es el código de 6 caracteres que identifica tu reserva. Lo necesitarás para realizar el Check-in."
            )

            AyudaItem(
                "¿Cuándo hacer el Check-in?",
                "El Check-in online está disponible desde 48 horas antes de tu vuelo. Solo necesitas tu PNR y apellido."
            )

            AyudaItem(
                "Seguridad (MFA)",
                "Puedes activar la verificación en dos pasos desde tu perfil para que tu cuenta esté más protegida mediante SMS."
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AzulPrincipal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Contacto de soporte", color = Blanco, fontWeight = FontWeight.Bold)
                    Text("soporte@aerolineastae.com", color = Blanco.copy(alpha = 0.8f))
                    Text("+503 2200-0000", color = Blanco.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun AyudaItem(pregunta: String, respuesta: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(pregunta, fontWeight = FontWeight.Bold, color = AzulPrincipal, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(respuesta, fontSize = 14.sp, color = GrisTexto)
        }
    }
}

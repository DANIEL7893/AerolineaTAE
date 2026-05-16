package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    onNavigate: (String) -> Unit
) {
    var codigoReserva by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Check-in",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulMuyClaro)
            )
        },
        bottomBar = {
            TaeBottomNavBar(currentRoute = "check-in", onNavigate = onNavigate)
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gestiona tu viaje",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Text(
                text = "Ingresa los datos para realizar el check-in o ver el estado de tu reserva.",
                fontSize = 14.sp,
                color = GrisTexto,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp).align(Alignment.Start)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = codigoReserva,
                        onValueChange = { if (it.length <= 6) codigoReserva = it.uppercase() },
                        label = { Text("Código de reserva") },
                        placeholder = { Text("Ej: ABCDEF") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulPrincipal,
                            focusedLabelColor = AzulPrincipal
                        )
                    )

                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido del pasajero") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulPrincipal,
                            focusedLabelColor = AzulPrincipal
                        )
                    )

                    Button(
                        onClick = { /* Acción de check-in */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
                    ) {
                        Text("Buscar reserva", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Información adicional
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AzulClaro.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AzulClaro
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "El check-in online está disponible entre 48 horas y 1 hora antes de la salida de tu vuelo.",
                        fontSize = 12.sp,
                        color = AzulOscuro
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckInScreenPreview() {
    TaeTheme {
        CheckInScreen(onNavigate = {})
    }
}

package com.dan.aerolineaTAE.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var codigoReserva by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check-in", color = Blanco, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
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
                        label = { Text("Código de reserva (PNR)") },
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
                        onClick = {
                            if (codigoReserva.length == 6 && apellido.isNotEmpty()) {
                                cargando = true
                                val authActual = com.google.firebase.auth.FirebaseAuth.getInstance()
                                val currentUid = authActual.currentUser?.uid ?: ""

                                // Filtramos por PNR y el ID del usuario actual
                                db.collection("reservas")
                                    .whereEqualTo("pnr", codigoReserva)
                                    .whereEqualTo("userId", currentUid)
                                    .get()
                                    .addOnSuccessListener { result ->
                                        if (!result.isEmpty) {
                                            val doc = result.documents[0]
                                            val reserva = doc.toObject(com.dan.aerolineaTAE.data.Reserva::class.java)
                                            
                                            if (reserva != null) {
                                                // Validamos que haya pasado al menos 1 minuto desde la compra
                                                val diff = System.currentTimeMillis() - reserva.fechaCompra
                                                if (diff < 60000) {
                                                    cargando = false
                                                    val sec = (60000 - diff) / 1000
                                                    Toast.makeText(context, "Disponible en $sec segundos.", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    db.collection("reservas").document(doc.id)
                                                        .update("estado", "Check-in realizado")
                                                        .addOnSuccessListener {
                                                            cargando = false
                                                            onNavigate("boarding_pass/$codigoReserva")
                                                        }
                                                }
                                            }
                                        } else {
                                            cargando = false
                                            Toast.makeText(context, "Reserva no encontrada o acceso denegado", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    .addOnFailureListener {
                                        cargando = false
                                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                        enabled = !cargando && codigoReserva.length == 6 && apellido.isNotEmpty()
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Realizar Check-in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AzulClaro.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AzulClaro)
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

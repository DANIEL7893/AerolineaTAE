package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Reserva
import com.dan.aerolineaTAE.data.Usuario
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardingPassScreen(
    pnr: String,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var reserva by remember { mutableStateOf<Reserva?>(null) }
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(pnr, userId) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).get().addOnSuccessListener { userDoc ->
                usuario = userDoc.toObject(Usuario::class.java)
                
                db.collection("reservas")
                    .whereEqualTo("pnr", pnr)
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            reserva = result.documents[0].toObject(Reserva::class.java)
                        }
                        cargando = false
                    }
                    .addOnFailureListener { cargando = false }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pase de Abordar", color = Blanco, fontWeight = FontWeight.Bold) },
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
        } else if (reserva == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró la información del pase de abordar.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AirplaneTicket, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(40.dp))
                            Text(
                                text = "TAE AIRLINES",
                                fontWeight = FontWeight.ExtraBold,
                                color = AzulPrincipal,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("PASAJERO", fontSize = 10.sp, color = GrisTexto)
                                Text("${usuario?.nombre} ${usuario?.apellido}".uppercase(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("PNR", fontSize = 10.sp, color = GrisTexto)
                                Text(reserva!!.pnr, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AzulPrincipal)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = AzulMuyClaro)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("DESDE", fontSize = 10.sp, color = GrisTexto)
                                Text(reserva!!.origen, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = AzulOscuro)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AirplaneTicket, contentDescription = null, tint = AzulClaro, modifier = Modifier.size(24.dp))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("HACIA", fontSize = 10.sp, color = GrisTexto)
                                Text(reserva!!.destino, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = AzulOscuro)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("FECHA", fontSize = 10.sp, color = GrisTexto)
                                Text(reserva!!.fecha, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("HORA", fontSize = 10.sp, color = GrisTexto)
                                Text(reserva!!.horaSalida, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("PUERTA", fontSize = 10.sp, color = GrisTexto)
                                Text("B12", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(AzulMuyClaro, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(100.dp), tint = AzulOscuro)
                                Text("Escanea en puerta", fontSize = 10.sp, color = GrisTexto)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "¡Buen viaje con Aerolíneas TAE!",
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

package com.dan.aerolineaTAE.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Reserva
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisViajesScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    
    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("reservas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    reservas = result.mapNotNull { it.toObject(Reserva::class.java).copy(id = it.id) }
                    cargando = false
                }
                .addOnFailureListener {
                    cargando = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis viajes", color = Blanco, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        bottomBar = {
            TaeBottomNavBar(currentRoute = "mis_viajes", onNavigate = onNavigate)
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AzulPrincipal)
            } else if (reservas.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AirplaneTicket, 
                        contentDescription = null, 
                        modifier = Modifier.size(64.dp), 
                        tint = AzulClaro
                    )
                    Text("Aún no tienes viajes programados", color = GrisTexto, modifier = Modifier.padding(16.dp))
                    Button(onClick = { onNavigate("buscar") }) {
                        Text("Buscar vuelos")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservas) { reserva ->
                        CardReserva(reserva = reserva)
                    }
                }
            }
        }
    }
}

@Composable
fun CardReserva(reserva: Reserva) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CÓDIGO DE RESERVA (PNR)", fontSize = 10.sp, color = GrisTexto)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("PNR", reserva.pnr)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(reserva.pnr, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AzulPrincipal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = AzulClaro, modifier = Modifier.size(16.dp))
                    }
                }
                Surface(
                    color = AzulMuyClaro,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = reserva.estado, 
                        color = AzulPrincipal, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AzulMuyClaro)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = AzulClaro)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("${reserva.origen} ➔ ${reserva.destino}", fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Text("Fecha: ${reserva.fecha} | Hora: ${reserva.horaSalida}", fontSize = 12.sp, color = GrisTexto)
                }
            }
        }
    }
}

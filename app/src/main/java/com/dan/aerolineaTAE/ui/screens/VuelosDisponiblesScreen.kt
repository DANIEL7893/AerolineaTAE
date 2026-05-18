package com.dan.aerolineaTAE.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Reserva
import com.dan.aerolineaTAE.data.Vuelo
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VuelosDisponiblesScreen(
    destinoId: String,
    onBack: () -> Unit,
    onComprar: (Vuelo) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var vuelos by remember { mutableStateOf<List<Vuelo>>(emptyList()) }
    var misVueloIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(destinoId, userId) {
        cargando = true
        
        if (userId.isNotEmpty()) {
            db.collection("reservas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    misVueloIds = result.mapNotNull { it.getString("vueloId") }.toSet()
                }
        }

        db.collection("vuelos")
            .whereEqualTo("destId", destinoId)
            .get()
            .addOnSuccessListener { result ->
                vuelos = result.mapNotNull { it.toObject(Vuelo::class.java).copy(id = it.id) }
                cargando = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vuelos a $destinoId", color = Blanco, fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AzulPrincipal)
            } else if (vuelos.isEmpty()) {
                Text("No hay vuelos disponibles.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vuelos) { vuelo ->
                        val yaComprado = misVueloIds.contains(vuelo.id)
                        CardVuelo(
                            vuelo = vuelo,
                            yaComprado = yaComprado,
                            onComprar = { onComprar(vuelo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardVuelo(vuelo: Vuelo, yaComprado: Boolean, onComprar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = vuelo.horaSalida, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Text(text = vuelo.origen, fontSize = 14.sp, color = GrisTexto)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Flight, contentDescription = null, tint = AzulClaro, modifier = Modifier.size(20.dp))
                    Box(modifier = Modifier.width(60.dp).height(1.dp).background(AzulMuyClaro))
                    Text(text = "Directo", fontSize = 10.sp, color = AzulClaro)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = vuelo.horaLlegada, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Text(text = vuelo.destino, fontSize = 14.sp, color = GrisTexto)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AzulMuyClaro)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = vuelo.aerolinea, fontSize = 12.sp, color = AzulClaro)
                    Text(text = "$${vuelo.precio}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AzulPrincipal)
                }
                
                if (yaComprado) {
                    Text(
                        text = "YA COMPRADO",
                        color = AzulClaro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Button(
                        onClick = onComprar,
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Comprar")
                    }
                }
            }
        }
    }
}

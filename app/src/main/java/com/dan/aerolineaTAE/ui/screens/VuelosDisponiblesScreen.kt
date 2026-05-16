package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Vuelo
import com.dan.aerolineaTAE.data.vuelosPrueba
import com.dan.aerolineaTAE.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VuelosDisponiblesScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vuelos Disponibles", color = Blanco, fontWeight = FontWeight.Bold) },
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
        ) {
            Text(
                text = "Selecciona tu vuelo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vuelosPrueba) { vuelo ->
                    CardVuelo(vuelo = vuelo)
                }
            }
        }
    }
}

@Composable
fun CardVuelo(vuelo: Vuelo) {
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
                    Box(modifier = Modifier.width(60.dp).height(1.dp).padding(vertical = 4.dp))
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
                Text(text = vuelo.aerolinea, fontSize = 12.sp, color = AzulClaro, fontWeight = FontWeight.Medium)
                Text(
                    text = "$${vuelo.precio}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulPrincipal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VuelosDisponiblesScreenPreview() {
    TaeTheme {
        VuelosDisponiblesScreen(onBack = {})
    }
}

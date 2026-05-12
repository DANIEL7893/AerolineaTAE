package com.dan.walletlogin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.walletlogin.ui.components.TaeBottomNavBar
import com.dan.walletlogin.ui.components.TarjetaVuelo
import com.dan.walletlogin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisViajesScreen(
    viajes: List<Any>, // TODO: conectar con Firestore
    onNavigate: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos", "Anteriores")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis viajes", color = Blanco, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        bottomBar = {
            TaeBottomNavBar(
                currentRoute = "mis_viajes",
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
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Blanco,
                contentColor = AzulPrincipal,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AzulPrincipal
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            if (viajes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AirplaneTicket,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = AzulClaro.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tenés viajes aún",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuro
                        )
                        Text(
                            text = "Tus reservas aparecerán aquí",
                            fontSize = 14.sp,
                            color = GrisTexto
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { onNavigate("buscar") },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
                        ) {
                            Text("Buscar vuelos")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (selectedTab == 0) {
                        items(viajes) { _ ->
                            TarjetaVuelo(
                                origen = "SAL",
                                destino = "MIA",
                                ciudadOrigen = "San Salvador",
                                ciudadDestino = "Miami",
                                fecha = "24 Oct 2026",
                                hora = "08:30 AM",
                                duracion = "2h 45m",
                                precio = "$350.00",
                                estado = "Confirmado"
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No hay viajes anteriores",
                                modifier = Modifier.padding(16.dp),
                                color = GrisTexto
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisViajesEmptyPreview() {
    TaeTheme {
        MisViajesScreen(viajes = emptyList(), onNavigate = {})
    }
}

@Preview(showBackground = true)
@Composable
fun MisViajesListPreview() {
    TaeTheme {
        MisViajesScreen(viajes = listOf(1), onNavigate = {})
    }
}

package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    email: String,
    onNavigate: (String) -> Unit
) {
    val nombre = email.split("@").firstOrNull() ?: "Usuario"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hola, $nombre",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = AzulClaro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulMuyClaro)
            )
        },
        bottomBar = {
            TaeBottomNavBar(currentRoute = "home", onNavigate = onNavigate)
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            // Card de búsqueda decorativa
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate("buscar") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AzulClaro)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "¿A dónde querés volar?", color = GrisTexto, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sección: Destinos populares
            item {
                Text(
                    text = "Destinos populares",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    val populares = listOf(
                        Triple("🇺🇸", "Miami", "$299"),
                        Triple("🇬🇹", "Guatemala", "$150"),
                        Triple("🇨🇴", "Bogotá", "$320"),
                        Triple("🇲🇽", "Cancún", "$210")
                    )
                    items(populares) { destino ->
                        DestinoCard(flag = destino.first, ciudad = destino.second, precio = destino.third)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sección: Ofertas especiales
            item {
                Text(
                    text = "Ofertas especiales",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            //Firestore
            items(2) { index ->
                val oferta = if (index == 0) "Vuela a Madrid" else "Escápate a Los Ángeles"
                val precioOriginal = if (index == 0) "$850" else "$550"
                val precioOferta = if (index == 0) "$699" else "$399"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulPrincipal)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = oferta, color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = precioOriginal,
                                color = Blanco.copy(alpha = 0.6f),
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = precioOferta,
                                color = Blanco,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DestinoCard(flag: String, ciudad: String, precio: String) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = flag, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = ciudad, fontWeight = FontWeight.Bold, color = AzulPrincipal)
            Text(text = "Desde $precio", fontSize = 12.sp, color = GrisTexto)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaeTheme {
        HomeScreen(email = "Daniel@gmail.com", onNavigate = {})
    }
}

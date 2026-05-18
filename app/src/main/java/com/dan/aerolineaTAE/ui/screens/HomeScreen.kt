package com.dan.aerolineaTAE.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.data.Oferta
import com.dan.aerolineaTAE.data.PopularDestino
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    email: String,
    onNavigate: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    var nombreUsuario by remember { mutableStateOf(email.split("@").firstOrNull() ?: "Usuario") }
    var populares by remember { mutableStateOf<List<PopularDestino>>(emptyList()) }
    var ofertas by remember { mutableStateOf<List<Oferta>>(emptyList()) }

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

        db.collection("destinos_populares").get().addOnSuccessListener { result ->
            populares = result.map { it.toObject(PopularDestino::class.java).copy(id = it.id) }
        }
        
        db.collection("ofertas").get().addOnSuccessListener { result ->
            ofertas = result.map { it.toObject(Oferta::class.java).copy(id = it.id) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hola, $nombreUsuario",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
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

            item {
                Text(
                    text = "Destinos populares",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                if (populares.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(populares) { destino ->
                            DestinoCard(
                                flag = destino.flag,
                                ciudad = destino.ciudad,
                                precio = destino.precio
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Ofertas especiales",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            items(ofertas) { oferta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulPrincipal)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = oferta.titulo, color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = oferta.precioOriginal,
                                color = Blanco.copy(alpha = 0.6f),
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = oferta.precioOferta,
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
            Text(text = flag, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = ciudad, fontWeight = FontWeight.Bold, color = AzulPrincipal, maxLines = 1)
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

package com.dan.walletlogin.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.walletlogin.data.Destino
import com.dan.walletlogin.data.destinosDisponibles
import com.dan.walletlogin.ui.components.TaeBottomNavBar
import com.dan.walletlogin.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarScreen(
    onNavigate: (String) -> Unit
) {
    var esIdaVuelta by remember { mutableStateOf(true) }
    var origen by remember { mutableStateOf<Destino?>(null) }
    var destino by remember { mutableStateOf<Destino?>(null) }
    var fechaIda by remember { mutableStateOf("") }
    var fechaVuelta by remember { mutableStateOf("") }
    var pasajeros by remember { mutableStateOf(1) }

    var mostrarSheet by remember { mutableStateOf(false) }
    var seleccionandoOrigen by remember { mutableStateOf(true) }
    var filtroDestino by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar vuelos", color = Blanco, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        bottomBar = {
            TaeBottomNavBar(currentRoute = "buscar", onNavigate = onNavigate)
        },
        containerColor = AzulMuyClaro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Toggle Ida / Ida y Vuelta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { esIdaVuelta = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!esIdaVuelta) AzulPrincipal else Blanco,
                        contentColor = if (!esIdaVuelta) Blanco else AzulPrincipal
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = if (esIdaVuelta) ButtonDefaults.outlinedButtonBorder else null
                ) {
                    Text("Solo ida")
                }
                Button(
                    onClick = { esIdaVuelta = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esIdaVuelta) AzulPrincipal else Blanco,
                        contentColor = if (esIdaVuelta) Blanco else AzulPrincipal
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = if (!esIdaVuelta) ButtonDefaults.outlinedButtonBorder else null
                ) {
                    Text("Ida y vuelta")
                }
            }

            // Card de Origen/Destino
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CampoBusqueda(
                        label = "Origen",
                        valor = origen?.let { "${it.ciudad} (${it.codigoIATA})" } ?: "Seleccionar ciudad",
                        icon = Icons.Default.FlightTakeoff,
                        onClick = {
                            seleccionandoOrigen = true
                            mostrarSheet = true
                        }
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Divider(color = AzulMuyClaro)
                        IconButton(
                            onClick = { 
                                val temp = origen
                                origen = destino
                                destino = temp
                            },
                            modifier = Modifier.background(Blanco, RoundedCornerShape(50.dp))
                        ) {
                            Icon(Icons.Default.SwapVert, contentDescription = null, tint = AzulClaro)
                        }
                    }

                    CampoBusqueda(
                        label = "Destino",
                        valor = destino?.let { "${it.ciudad} (${it.codigoIATA})" } ?: "Seleccionar ciudad",
                        icon = Icons.Default.FlightLand,
                        onClick = {
                            seleccionandoOrigen = false
                            mostrarSheet = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card de Fecha
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        CampoBusqueda(
                            label = "Fecha de ida",
                            valor = if (fechaIda.isEmpty()) "Elige fecha" else fechaIda,
                            icon = Icons.Default.CalendarMonth,
                            onClick = {
                                DatePickerDialog(context, { _, y, m, d -> fechaIda = "$d/${m+1}/$y" }, 
                                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                            }
                        )
                    }
                    if (esIdaVuelta) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            CampoBusqueda(
                                label = "Fecha de vuelta",
                                valor = if (fechaVuelta.isEmpty()) "Elige fecha" else fechaVuelta,
                                icon = Icons.Default.CalendarMonth,
                                onClick = {
                                    DatePickerDialog(context, { _, y, m, d -> fechaVuelta = "$d/${m+1}/$y" }, 
                                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card de Pasajeros
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = AzulClaro)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Pasajeros", fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (pasajeros > 1) pasajeros-- }) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = AzulClaro)
                        }
                        Text(text = pasajeros.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        IconButton(onClick = { if (pasajeros < 9) pasajeros++ }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = AzulClaro)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Lógica de búsqueda */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
            ) {
                Text("Buscar vuelos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarSheet) {
        ModalBottomSheet(
            onDismissRequest = { mostrarSheet = false },
            containerColor = Blanco
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.8f)) {
                Text(
                    text = if (seleccionandoOrigen) "Ciudad de origen" else "Ciudad de destino",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = filtroDestino,
                    onValueChange = { filtroDestino = it },
                    placeholder = { Text("Buscar ciudad o aeropuerto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    val filtrados = destinosDisponibles.filter { 
                        it.ciudad.contains(filtroDestino, true) || it.codigoIATA.contains(filtroDestino, true)
                    }
                    items(filtrados) { item ->
                        ListItem(
                            headlineContent = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.codigoIATA, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(item.ciudad, fontWeight = FontWeight.Medium)
                                }
                            },
                            supportingContent = { Text("${item.aeropuerto} | ${item.pais}") },
                            modifier = Modifier.clickable {
                                if (seleccionandoOrigen) origen = item else destino = item
                                mostrarSheet = false
                                filtroDestino = ""
                            }
                        )
                        Divider(color = AzulMuyClaro)
                    }
                }
            }
        }
    }
}

@Composable
fun CampoBusqueda(label: String, valor: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AzulClaro, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = GrisTexto)
            Text(text = valor, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BuscarScreenPreview() {
    TaeTheme {
        BuscarScreen(onNavigate = {})
    }
}

package com.dan.aerolineaTAE.ui.screens

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
import com.dan.aerolineaTAE.data.Destino
import com.dan.aerolineaTAE.data.destinosDisponibles
import com.dan.aerolineaTAE.ui.components.TaeBottomNavBar
import com.dan.aerolineaTAE.ui.theme.*
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
    var pasajeros by remember { mutableIntStateOf(1) }

    var mostrarSheet by remember { mutableStateOf(false) }
    var seleccionandoOrigen by remember { mutableStateOf(true) }
    var filtroDestino by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val mismaCiudad = origen != null && destino != null && origen?.codigoIATA == destino?.codigoIATA
    
    val dateSDF = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaIdaDate = try { dateSDF.parse(fechaIda) } catch (e: Exception) { null }
    val fechaVueltaDate = try { dateSDF.parse(fechaVuelta) } catch (e: Exception) { null }
    
    val fechaInvalida = esIdaVuelta && fechaIdaDate != null && fechaVueltaDate != null && fechaVueltaDate.before(fechaIdaDate)
    
    val puedeBuscar = origen != null && destino != null && fechaIda.isNotEmpty() && 
                     (!esIdaVuelta || fechaVuelta.isNotEmpty()) && !mismaCiudad && !fechaInvalida

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
                    shape = RoundedCornerShape(8.dp)
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
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ida y vuelta")
                }
            }

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
                        HorizontalDivider(color = AzulMuyClaro)
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
            
            if (mismaCiudad) {
                Text(
                    "El origen y destino no pueden ser iguales",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        CampoBusqueda(
                            label = "Fecha de ida",
                            valor = fechaIda.ifEmpty { "Elige fecha" },
                            icon = Icons.Default.CalendarMonth,
                            onClick = {
                                val dialog = DatePickerDialog(context, { _, y, m, d -> 
                                    fechaIda = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y) 
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                                dialog.datePicker.minDate = System.currentTimeMillis() - 1000
                                dialog.show()
                            }
                        )
                    }
                    if (esIdaVuelta) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            CampoBusqueda(
                                label = "Fecha de vuelta",
                                valor = fechaVuelta.ifEmpty { "Elige fecha" },
                                icon = Icons.Default.CalendarMonth,
                                onClick = {
                                    val dialog = DatePickerDialog(context, { _, y, m, d -> 
                                        fechaVuelta = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y) 
                                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                                    
                                    val minDate = fechaIdaDate?.time ?: System.currentTimeMillis()
                                    dialog.datePicker.minDate = minDate - 1000
                                    dialog.show()
                                }
                            )
                        }
                    }
                }
            }
            
            if (fechaInvalida) {
                Text(
                    "La fecha de regreso debe ser igual o posterior a la de ida",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                onClick = { 
                    if (puedeBuscar) {
                        onNavigate("vuelos_disponibles/${destino?.codigoIATA}")
                    }
                },
                enabled = puedeBuscar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPrincipal,
                    disabledContainerColor = GrisTexto.copy(alpha = 0.3f)
                )
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
                        HorizontalDivider(color = AzulMuyClaro)
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

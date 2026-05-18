package com.dan.aerolineaTAE.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dan.aerolineaTAE.ui.theme.*

@Composable
fun TarjetaVuelo(
    origen: String,
    destino: String,
    ciudadOrigen: String,
    ciudadDestino: String,
    fecha: String,
    hora: String,
    duracion: String,
    precio: String,
    estado: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = origen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulPrincipal
                )
                
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = AzulClaro,
                    modifier = Modifier.size(24.dp)
                )
                
                Text(
                    text = destino,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulPrincipal
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = ciudadOrigen, fontSize = 12.sp, color = GrisTexto)
                Text(text = ciudadDestino, fontSize = 12.sp, color = GrisTexto)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = AzulMuyClaro, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row {
                        Text(text = "$fecha | $hora", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Text(text = "Duración: $duracion", fontSize = 12.sp, color = GrisTexto)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val badgeColor = if (estado == "Confirmado") VerdeConfirmado else AmarilloEstado
                    Surface(
                        color = badgeColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = estado,
                            color = badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = precio,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulPrincipal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaVueloPreview() {
    TaeTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TarjetaVuelo(
                origen = "SAL",
                destino = "MIA",
                ciudadOrigen = "San Salvador",
                ciudadDestino = "Miami",
                fecha = "15 Jul 2026",
                hora = "08:30",
                duracion = "2h 45min",
                precio = "$189",
                estado = "Confirmado"
            )
        }
    }
}

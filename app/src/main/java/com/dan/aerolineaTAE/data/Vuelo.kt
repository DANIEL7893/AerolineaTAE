package com.dan.aerolineaTAE.data

data class Vuelo(
    val id: String = "",
    val origen: String = "",
    val destino: String = "",
    val destId: String = "",
    val fecha: String = "",
    val horaSalida: String = "",
    val horaLlegada: String = "",
    val precio: Double = 0.0,
    val aerolinea: String = "TAE Airlines"
)

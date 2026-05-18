package com.dan.aerolineaTAE.data

data class Reserva(
    val id: String = "",
    val userId: String = "",
    val vueloId: String = "",
    val pnr: String = "",
    val origen: String = "",
    val destino: String = "",
    val fecha: String = "",
    val horaSalida: String = "",
    val precio: Double = 0.0,
    val fechaCompra: Long = 0L, // Timestamp de la compra
    val estado: String = "Confirmado" // Confirmado, Check-in realizado, etc.
)

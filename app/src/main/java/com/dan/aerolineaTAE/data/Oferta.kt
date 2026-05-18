package com.dan.aerolineaTAE.data

data class Oferta(
    val id: String = "",
    val titulo: String = "",
    val precioOriginal: String = "",
    val precioOferta: String = ""
)

data class PopularDestino(
    val id: String = "",
    val flag: String = "",
    val ciudad: String = "",
    val precio: String = "",
    val destId: String = ""
)

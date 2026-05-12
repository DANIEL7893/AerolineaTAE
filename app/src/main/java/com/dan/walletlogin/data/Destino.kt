package com.dan.walletlogin.data

data class Destino(
    val ciudad: String,
    val pais: String,
    val codigoIATA: String,
    val aeropuerto: String
)

val destinosDisponibles = listOf(
    Destino("San Salvador", "El Salvador", "SAL", "Aeropuerto Internacional El Salvador"),
    Destino("Ciudad de Guatemala", "Guatemala", "GUA", "Aeropuerto La Aurora"),
    Destino("Tegucigalpa", "Honduras", "TGU", "Aeropuerto Toncontín"),
    Destino("Managua", "Nicaragua", "MGA", "Aeropuerto Augusto Sandino"),
    Destino("San José", "Costa Rica", "SJO", "Aeropuerto Juan Santamaría"),
    Destino("Ciudad de Panamá", "Panamá", "PTY", "Aeropuerto Tocumen"),
    Destino("Cancún", "México", "CUN", "Aeropuerto Internacional Cancún"),
    Destino("Ciudad de México", "México", "MEX", "Aeropuerto Benito Juárez"),
    Destino("Miami", "Estados Unidos", "MIA", "Aeropuerto Internacional Miami"),
    Destino("Los Ángeles", "Estados Unidos", "LAX", "Aeropuerto Internacional LAX"),
    Destino("Madrid", "España", "MAD", "Aeropuerto Adolfo Suárez Barajas")
)

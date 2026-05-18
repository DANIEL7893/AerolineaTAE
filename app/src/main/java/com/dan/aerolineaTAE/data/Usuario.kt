package com.dan.aerolineaTAE.data

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val pin: String = "",
    val mfaEnabled: Boolean = false,
    val mfaType: String = "none" // "pin" o "biometric"
)

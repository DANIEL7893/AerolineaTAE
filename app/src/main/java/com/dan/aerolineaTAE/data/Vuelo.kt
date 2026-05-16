package com.dan.aerolineaTAE.data

data class Vuelo(
    val id: String,
    val origen: String,
    val destino: String,
    val fecha: String,
    val horaSalida: String,
    val horaLlegada: String,
    val precio: Double,
    val aerolinea: String = "TAE Airlines"
)

val vuelosPrueba = listOf(
    // El Salvador
    Vuelo("TAE001", "SAL", "MEX", "20/10/2023", "08:00", "11:30", 250.0),
    Vuelo("TAE002", "SAL", "GUA", "20/10/2023", "14:00", "14:45", 120.0),
    Vuelo("TAE003", "SAL", "MIA", "21/10/2023", "07:30", "12:00", 300.0),
    
    // Guatemala
    Vuelo("TAE004", "GUA", "SAL", "20/10/2023", "09:00", "09:45", 115.0),
    Vuelo("TAE005", "GUA", "CUN", "20/10/2023", "12:00", "13:30", 180.0),
    Vuelo("TAE006", "GUA", "PTY", "21/10/2023", "15:00", "17:45", 220.0),
    
    // México
    Vuelo("TAE007", "MEX", "SAL", "20/10/2023", "13:00", "16:30", 240.0),
    Vuelo("TAE008", "MEX", "MAD", "20/10/2023", "20:00", "14:00", 850.0),
    Vuelo("TAE009", "MEX", "LAX", "21/10/2023", "10:00", "13:00", 350.0),
    
    // Costa Rica
    Vuelo("TAE010", "SJO", "PTY", "20/10/2023", "08:00", "09:15", 150.0),
    Vuelo("TAE011", "SJO", "SAL", "20/10/2023", "16:00", "17:30", 190.0),
    Vuelo("TAE012", "SJO", "MIA", "21/10/2023", "11:00", "15:30", 280.0)
)

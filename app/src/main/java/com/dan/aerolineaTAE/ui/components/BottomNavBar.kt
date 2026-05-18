package com.dan.aerolineaTAE.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.dan.aerolineaTAE.ui.theme.*

sealed class BottomBarItem(val route: String, val title: String, val icon: ImageVector) {
    object Inicio : BottomBarItem("home", "Inicio", Icons.Default.Home)
    object Reservar : BottomBarItem("buscar", "Reservar", Icons.Default.Search)
    object CheckIn : BottomBarItem("check-in", "Check-In", Icons.Default.AirplaneTicket)
    object MisViajes : BottomBarItem("mis_viajes", "Mis viajes", Icons.Default.AirplanemodeActive)
    object Perfil : BottomBarItem("perfil", "Perfil", Icons.Default.Person)
}

@Composable
fun TaeBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomBarItem.Inicio,
        BottomBarItem.Reservar,
        BottomBarItem.CheckIn,
        BottomBarItem.MisViajes,
        BottomBarItem.Perfil
    )

    NavigationBar(
        containerColor = AzulPrincipal,
        contentColor = Blanco
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulPrincipal,
                    selectedTextColor = Blanco,
                    indicatorColor = AzulMuyClaro,
                    unselectedIconColor = Blanco,
                    unselectedTextColor = Blanco
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaeBottomNavBarPreview() {
    TaeTheme {
        TaeBottomNavBar(
            currentRoute = "buscar",
            onNavigate = {}
        )
    }
}

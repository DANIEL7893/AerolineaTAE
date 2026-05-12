package com.dan.walletlogin.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    secondary = AzulClaro,
    onSecondary = Blanco,
    tertiary = AzulOscuro,
    background = AzulMuyClaro,
    surface = Blanco,
    onSurface = AzulOscuro,
    surfaceVariant = AzulMuyClaro,
    error = RojoError
)

@Composable
fun TaeTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

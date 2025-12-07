package com.example.proyectofinaldegrado.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 1. Defino tus colores como constantes de Compose
private val BeigePrimary = Color(0xFFE19580)      // Tu 'default_button'
private val BeigeAccent = Color(0xFFB35C44)       // Tu 'beige_accent'
private val AppBackground = Color(0xFFFAE9E5)     // Tu 'background_color'
private val TextDark = Color(0xFF090909)            // Tu 'text_dark'

// Un color ligeramente más oscuro para el fondo de las tarjetas
private val SurfaceVariantLight = Color(0xFFF0E0DC) 

// 2. Creo un ColorScheme de Material 3 usando tus colores
private val LightColorScheme = lightColorScheme(
    primary = BeigePrimary,
    secondary = BeigeAccent,
    background = AppBackground,
    surface = AppBackground,
    onPrimary = TextDark,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariantLight
)

// 3. Creo un Composable de Tema reutilizable
@Composable
fun ProyectoFinalDeGradoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        // Por ahora usamos la tipografía por defecto de Material
        // typography = Typography, 
        content = content
    )
}

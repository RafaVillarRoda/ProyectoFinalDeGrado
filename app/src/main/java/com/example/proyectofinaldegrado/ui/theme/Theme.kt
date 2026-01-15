package com.example.proyectofinaldegrado.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val BeigePrimary = Color(0xFFE19580)      // Tu 'default_button'
private val BeigeAccent = Color(0xFFB35C44)       // Tu 'beige_accent'
private val AppBackground = Color(0xFFFAE9E5)     // Tu 'background_color'
private val TextDark = Color(0xFF090909)            // Tu 'text_dark'

private val DarkPrimary = Color(0xFFC48B79)
private val DarkBackground = Color(0xFF2E2421)

private val DarkSurface = Color(0xFF3A2D29)

private val DarkAccent = Color(0xFF9E5440)

private val TextLight = Color(0xFFF5F0EE)
private val SurfaceVariantLight = Color(0xFFF0E0DC) 


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

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = DarkSurface
)


@Composable
fun ProyectoFinalDeGradoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    MaterialTheme(
        colorScheme =colorScheme,
        content = content
    )
}

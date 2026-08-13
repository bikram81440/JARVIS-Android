package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JarvisDarkColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = JarvisNavy,
    primaryContainer = JarvisBlue,
    onPrimaryContainer = Color.White,
    secondary = JarvisGold,
    onSecondary = JarvisNavy,
    tertiary = JarvisBlue,
    background = JarvisNavy,
    onBackground = JarvisTextPrimary,
    surface = JarvisSurface,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisCard,
    onSurfaceVariant = JarvisTextSecondary,
    error = JarvisError,
    onError = Color.White
)

@Composable
fun JarvisTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisDarkColorScheme,
        typography = Typography,
        content = content
    )
}

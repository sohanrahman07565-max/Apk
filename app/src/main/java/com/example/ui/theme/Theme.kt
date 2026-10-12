package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CyberDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = CyberCyanGlow,
    secondary = CyberPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A148C),
    onSecondaryContainer = CyberPurpleGlow,
    tertiary = CyberGreen,
    onTertiary = Color(0xFF003915),
    background = CyberDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = CyberCardBg,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = CyberCardBorder
)

private val CyberLightColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = CyberCyanGlow,
    secondary = CyberPurple,
    onSecondary = Color.White,
    background = CyberDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = CyberCardBg,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = CyberCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep cybernetic theme constant for driver tool aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) CyberDarkColorScheme else CyberLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

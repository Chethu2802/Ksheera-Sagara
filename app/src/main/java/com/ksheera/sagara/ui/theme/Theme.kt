package com.ksheera.sagara.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Refined emerald + teal palette with warm amber accent
private val Emerald = Color(0xFF10B981)
private val EmeraldDeep = Color(0xFF047857)
private val Teal = Color(0xFF0F766E)
private val Amber = Color(0xFFF59E0B)
private val AmberSoft = Color(0xFFFFE0A3)
private val BgLight = Color(0xFFF6FBF7)
private val SurfaceLight = Color(0xFFFFFFFF)
private val Ink = Color(0xFF0F1F1A)
private val InkSoft = Color(0xFF4B5563)

private val Light = lightColorScheme(
    primary = Emerald, onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5), onPrimaryContainer = EmeraldDeep,
    secondary = Amber, onSecondary = Color(0xFF1F1300),
    secondaryContainer = AmberSoft, onSecondaryContainer = Color(0xFF3B2A00),
    tertiary = Teal, onTertiary = Color.White,
    background = BgLight, onBackground = Ink,
    surface = SurfaceLight, onSurface = Ink,
    surfaceVariant = Color(0xFFE6F4EC), onSurfaceVariant = InkSoft,
    outline = Color(0xFFB7D4C5)
)

private val Dark = darkColorScheme(
    primary = Emerald, onPrimary = Color(0xFF002417),
    primaryContainer = EmeraldDeep, onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Amber, onSecondary = Color(0xFF1F1300),
    tertiary = Teal,
    background = Color(0xFF0B1612), onBackground = Color(0xFFE6F4EC),
    surface = Color(0xFF12201B), onSurface = Color(0xFFE6F4EC),
    surfaceVariant = Color(0xFF1A2C25), onSurfaceVariant = Color(0xFFB7D4C5)
)

@Composable
fun KsheeraTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}

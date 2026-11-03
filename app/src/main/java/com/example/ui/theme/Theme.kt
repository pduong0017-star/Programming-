package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VibeCutColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF00201B),
    primaryContainer = Color(0xFF004D42),
    onPrimaryContainer = ElectricCyan,
    secondary = NeonMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A0E2B),
    onSecondaryContainer = Color(0xFFFFB1C8),
    tertiary = SolarGold,
    onTertiary = Color(0xFF261A00),
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = CoralError,
    onError = Color.White,
    outline = StudioSurfaceBright
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VibeCutColorScheme,
        typography = Typography,
        content = content
    )
}

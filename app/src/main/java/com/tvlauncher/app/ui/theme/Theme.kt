package com.tvlauncher.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

val LauncherBackground = Color(0xFF0A0A0F)
val LauncherSurface = Color(0xFF1A1A2E)
val LauncherSurfaceVariant = Color(0xFF16213E)
val LauncherPrimary = Color(0xFF4FC3F7)
val LauncherSecondary = Color(0xFF81C784)
val FocusGlow = Color(0xFF4FC3F7)
val FocusBorder = Color(0xFF29B6F6)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TVLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = LauncherPrimary,
            secondary = LauncherSecondary,
            background = LauncherBackground,
            surface = LauncherSurface,
            surfaceVariant = LauncherSurfaceVariant,
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

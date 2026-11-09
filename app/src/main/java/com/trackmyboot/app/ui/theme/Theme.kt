package com.trackmyboot.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PitchGreen = Color(0xFF0B7A43)
val PitchGreenDark = Color(0xFF064D2A)
val NeonLime = Color(0xFFC6F135)
val DealRed = Color(0xFFE5383B)

private val Light = lightColorScheme(
    primary = PitchGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9F2CD),
    onPrimaryContainer = Color(0xFF00210F),
    secondary = Color(0xFF4A6354),
    secondaryContainer = Color(0xFFE4F7B0),
    onSecondaryContainer = Color(0xFF1A2400),
    tertiary = Color(0xFF5B6300),
    error = DealRed,
    background = Color(0xFFF6FBF5),
    surface = Color(0xFFF6FBF5),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF7FDBA2),
    onPrimary = Color(0xFF00391C),
    primaryContainer = PitchGreenDark,
    onPrimaryContainer = Color(0xFFB9F2CD),
    secondary = Color(0xFFB2CCBB),
    secondaryContainer = Color(0xFF3B4A00),
    onSecondaryContainer = NeonLime,
    tertiary = NeonLime,
    error = Color(0xFFFF8A80),
    background = Color(0xFF0F1511),
    surface = Color(0xFF0F1511),
)

@Composable
fun TrackmyBootTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}

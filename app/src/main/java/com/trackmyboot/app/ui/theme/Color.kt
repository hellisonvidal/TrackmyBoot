package com.trackmyboot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================================
// OPÇÃO 3: "GRAMADO PREMIUM" - Elegante Minimalista
// Verde musgo + Dourado suave - Sofisticado, clean
// ============================================================
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),      // Verde musgo elegante
    primaryContainer = Color(0xFFDCEDC8),
    secondary = Color(0xFFF57F17),    // Dourado suave
    secondaryContainer = Color(0xFFFFF8E1),
    tertiary = Color(0xFF558B2F),
    tertiaryContainer = Color(0xFFC8E6C9),
    surface = Color(0xFFFAFAFA),      // Off-white suave
    surfaceContainerHighest = Color(0xFFF1F8E9),
    error = Color(0xFFC62828),
    onPrimary = Color.White,
    onPrimaryContainer = Color(0xFF1B5E20),
    onSecondary = Color.White,
    onSecondaryContainer = Color(0xFFE65100),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF4A4A4A),
    outline = Color(0xFFE8EAF6),
    outlineVariant = Color(0xFFDCE4EC),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    primaryContainer = Color(0xFF2E7D32),
    secondary = Color(0xFFFFF176),
    secondaryContainer = Color(0xFFF57F17),
    tertiary = Color(0xFF81C784),
    tertiaryContainer = Color(0xFF1B5E20),
    surface = Color(0xFF1B1B1B),
    surfaceContainerHighest = Color(0xFF2C2C2C),
    error = Color(0xFFEF5350),
    onPrimary = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    onSecondary = Color(0xFF1B1B1B),
    onSecondaryContainer = Color(0xFFFFF8E1),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF616161),
)

@Composable
fun Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
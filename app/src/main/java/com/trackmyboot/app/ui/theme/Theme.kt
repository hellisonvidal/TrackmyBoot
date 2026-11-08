package com.trackmyboot.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

@Composable
fun ThemeSurface(
    color: Color = MaterialTheme.colorScheme.surface,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        modifier = modifier,
        content = content
    )
}

// Extension to get theme colors easily
val androidx.compose.ui.Modifier.themeSurface: androidx.compose.ui.Modifier
    get() = this.background(MaterialTheme.colorScheme.surface)

val Activity.isDarkTheme: Boolean
    get() = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
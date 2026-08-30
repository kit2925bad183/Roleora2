package com.example.roleora.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = PolishPrimaryLight,
    onPrimary = PolishOnPrimaryContainer,
    primaryContainer = PolishPrimary,
    onPrimaryContainer = PolishPrimaryContainer,
    secondary = PolishSecondaryContainer,
    onSecondary = PolishOnSecondaryContainer,
    secondaryContainer = PolishSecondary,
    onSecondaryContainer = PolishPrimaryLight,
    tertiary = DirectorGold,
    onTertiary = Color.White,
    background = PolishDarkCanvas,
    onBackground = PolishDarkTextPrimary,
    surface = PolishDarkSurface,
    onSurface = PolishDarkTextPrimary,
    surfaceVariant = PolishDarkSurfaceVariant,
    onSurfaceVariant = PolishDarkTextSecondary,
    outline = PolishDarkOutline,
    outlineVariant = PolishDarkOutlineVariant,
    error = PolishRed,
    onError = Color.White,
    errorContainer = PolishRedContainer,
    onErrorContainer = PolishRed
)

private val LightColorScheme = lightColorScheme(
    primary = PolishPrimary,
    onPrimary = PolishOnPrimary,
    primaryContainer = PolishPrimaryContainer,
    onPrimaryContainer = PolishOnPrimaryContainer,
    secondary = PolishSecondary,
    onSecondary = PolishOnSecondary,
    secondaryContainer = PolishSecondaryContainer,
    onSecondaryContainer = PolishOnSecondaryContainer,
    tertiary = DirectorGold,
    onTertiary = Color.White,
    background = PolishCanvasLight,
    onBackground = PolishTextPrimary,
    surface = PolishSurfaceLight,
    onSurface = PolishTextPrimary,
    surfaceVariant = PolishSurfaceVariant,
    onSurfaceVariant = PolishTextSecondary,
    outline = PolishOutline,
    outlineVariant = PolishOutlineVariant,
    error = PolishRed,
    onError = Color.White,
    errorContainer = PolishRedContainer,
    onErrorContainer = PolishRed
)

@Composable
fun RoleoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

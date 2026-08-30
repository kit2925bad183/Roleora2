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

private val DarkColorScheme = darkColorScheme(
    primary = LuminousViolet,
    onPrimary = Color.White,
    primaryContainer = LuminousVioletDark,
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = TealAccent,
    onSecondary = Color.Black,
    secondaryContainer = TealAccentDark,
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = DirectorGold,
    onTertiary = Color.Black,
    background = MidnightNavy,
    onBackground = Color(0xFFF1F5F9),
    surface = MidnightSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = MidnightElevated,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = MidnightBorder,
    outlineVariant = Color(0xFF1E293B),
    error = CoralRed,
    onError = Color.White,
    errorContainer = CoralRedSubtle,
    onErrorContainer = Color(0xFFFEE2E2)
)

private val LightColorScheme = lightColorScheme(
    primary = LuminousVioletDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = LuminousVioletDark,
    secondary = TealAccentDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = TealAccentDark,
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = LightCanvas,
    onBackground = GraphiteText,
    surface = LightSurface,
    onSurface = GraphiteText,
    surfaceVariant = LightElevated,
    onSurfaceVariant = GraphiteMuted,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = CoralRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = CoralRedSubtle
)

@Composable
fun RoleoraTheme(
    darkTheme: Boolean = true, // ROLEORA looks signature in Dark/Midnight Theme by default
    dynamicColor: Boolean = false, // Keep intentional brand palette
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

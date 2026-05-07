package com.example.trigen.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    primaryContainer = RedDark,
    secondary = SteelBlue,
    onSecondary = White,
    tertiary = TealPrimary,
    background = NavyDark,
    onBackground = SilverGrey,
    surface = NavyMid,
    onSurface = White,
    error = RedPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = White,
    primaryContainer = RedLight,
    secondary = SteelBlue,
    onSecondary = White,
    secondaryContainer = SteelBlueLight,
    tertiary = TealDark,
    tertiaryContainer = TealLight,
    background = SilverLight,
    onBackground = NavyDark,
    surface = White,
    onSurface = NavyDark,
    surfaceVariant = SilverLight,
    error = RedPrimary
)

@Composable
fun TriGenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
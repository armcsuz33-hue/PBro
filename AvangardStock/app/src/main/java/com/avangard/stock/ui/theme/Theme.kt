package com.avangard.stock.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AvangardTeal,
    onPrimary = Color.White,
    primaryContainer = AvangardTealLight,
    onPrimaryContainer = Color.White,
    secondary = AvangardGold,
    onSecondary = Color.Black,
    secondaryContainer = AvangardGoldDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    error = AlertRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AvangardTealLight,
    onPrimary = Color.Black,
    primaryContainer = AvangardTealDark,
    onPrimaryContainer = Color.White,
    secondary = AvangardGold,
    onSecondary = Color.Black,
    secondaryContainer = AvangardGoldDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun AvangardStockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

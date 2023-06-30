package com.daisy.jetclock.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = UltraViolet,
    primaryVariant = PortGore,

    secondary = Platinum,
    secondaryVariant = AfricanViolet,
    onSecondary = AfricanViolet,

    background = OxfordViolet,
    onBackground = Platinum,

    onSurface = TaupeGrey,

    error = Color.Red,
)

private val LightColorPalette = lightColors(
    primary = UltraViolet,
    primaryVariant = PortGore,

    secondary = Color.White,
    secondaryVariant = AfricanViolet,
    onSecondary = AfricanViolet,

    background = WildSand,
    onBackground = OxfordViolet,

    onSurface = CoolGrey,

    error = Color.Red,
)

@Composable
fun JetClockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()

            WindowCompat.getInsetsController(window, view)
                ?.isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view)
                ?.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
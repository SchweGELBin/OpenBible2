package com.schwegelbin.openbible.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight
)

private val AmoledColorScheme = darkColorScheme(
    primary = PrimaryAmoled,
    secondary = SecondaryAmoled,
    tertiary = TertiaryAmoled,
    background = Color.Black,
    surface = Color.Black,
    surfaceContainer = Color.Black,
    surfaceContainerLow = Color.Black
)

@Composable
fun OpenBibleTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    amoled: Boolean,
    content: @Composable () -> Unit
) {
    var colorScheme = when {
        amoled -> AmoledColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        colorScheme =
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        if (amoled) colorScheme = colorScheme.copy(
            background = AmoledColorScheme.background,
            surface = AmoledColorScheme.surface,
            surfaceContainer = AmoledColorScheme.surfaceContainer,
            surfaceContainerLow = AmoledColorScheme.surfaceContainerLow
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
}
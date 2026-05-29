package com.example.workly.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,

    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = OnPrimaryDark,
    onSecondary = OnPrimaryDark,
    onTertiary = OnPrimaryDark,

    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(

    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,

    background = LightBackground,
    surface = LightSurface,

    onPrimary = OnPrimaryLight,
    onSecondary = OnPrimaryLight,
    onTertiary = OnPrimaryLight,

    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight
)

@Composable
fun WorklyTheme(

    darkTheme: Boolean = false,

    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {

    val colorScheme = when {

        dynamicColor &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
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
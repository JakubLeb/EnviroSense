package com.example.envirosense.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Kolory aplikacji
val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)
val Green900 = Color(0xFF1B5E20)
val Teal500 = Color(0xFF009688)
val Teal700 = Color(0xFF00796B)

val LightGreen100 = Color(0xFFDCEDC8)
val LightGreen50 = Color(0xFFF1F8E9)

// AQI Colors
val AqiGood = Color(0xFF4CAF50)
val AqiFair = Color(0xFF8BC34A)
val AqiModerate = Color(0xFFFFEB3B)
val AqiPoor = Color(0xFFFF9800)
val AqiVeryPoor = Color(0xFFF44336)

private val LightColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = Color.White,
    primaryContainer = LightGreen100,
    onPrimaryContainer = Green900,
    secondary = Teal500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Teal700,
    tertiary = Color(0xFF795548),
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = Color.Black,
    primaryContainer = Green900,
    onPrimaryContainer = LightGreen100,
    secondary = Teal500,
    onSecondary = Color.Black,
    secondaryContainer = Teal700,
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFBCAAA4),
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun EnviroSenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        content = content
    )
}

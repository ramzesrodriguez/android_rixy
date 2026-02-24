package com.externalpods.rixy.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// Rixy Design System v4.0 — Theme
// ============================================================================

private val LightColorScheme = lightColorScheme(
    primary = RixyColors.Brand,
    onPrimary = Color.White,
    primaryContainer = RixyColors.Brand.copy(alpha = 0.1f),
    onPrimaryContainer = RixyColors.Brand,
    
    secondary = RixyColors.Structure,
    onSecondary = Color.White,
    secondaryContainer = RixyColors.Structure.copy(alpha = 0.1f),
    onSecondaryContainer = RixyColors.Structure,
    
    tertiary = RixyColors.Action,
    onTertiary = Color.White,
    tertiaryContainer = RixyColors.Action.copy(alpha = 0.1f),
    onTertiaryContainer = RixyColors.Action,
    
    background = RixyColors.Background,
    onBackground = RixyColors.TextPrimary,
    
    surface = RixyColors.Surface,
    onSurface = RixyColors.TextPrimary,
    surfaceVariant = RixyColors.Background,
    onSurfaceVariant = RixyColors.TextSecondary,
    
    error = RixyColors.Error,
    onError = Color.White,
    errorContainer = RixyColors.Error.copy(alpha = 0.1f),
    onErrorContainer = RixyColors.Error,
    
    outline = RixyColors.Border,
    outlineVariant = RixyColors.Border.copy(alpha = 0.5f),
    
    scrim = Color.Black.copy(alpha = 0.5f),
    
    inverseSurface = RixyColors.Structure,
    inverseOnSurface = Color.White,
    inversePrimary = RixyColors.Brand
)

private val DarkColorScheme = darkColorScheme(
    primary = RixyColors.Brand,
    onPrimary = Color.White,
    primaryContainer = RixyColors.Brand.copy(alpha = 0.2f),
    onPrimaryContainer = RixyColors.Brand,
    
    secondary = RixyColors.Structure,
    onSecondary = Color.White,
    secondaryContainer = RixyColors.Structure.copy(alpha = 0.2f),
    onSecondaryContainer = RixyColors.Structure,
    
    tertiary = RixyColors.Action,
    onTertiary = Color.White,
    tertiaryContainer = RixyColors.Action.copy(alpha = 0.2f),
    onTertiaryContainer = RixyColors.Action,
    
    background = RixyColors.BackgroundDark,
    onBackground = RixyColors.TextPrimaryDark,
    
    surface = RixyColors.SurfaceDark,
    onSurface = RixyColors.TextPrimaryDark,
    surfaceVariant = RixyColors.SurfaceDark.copy(alpha = 0.5f),
    onSurfaceVariant = RixyColors.TextSecondaryDark,
    
    error = RixyColors.Error,
    onError = Color.White,
    errorContainer = RixyColors.Error.copy(alpha = 0.2f),
    onErrorContainer = RixyColors.Error,
    
    outline = RixyColors.BorderDark.copy(alpha = 0.25f),
    outlineVariant = RixyColors.BorderDark.copy(alpha = 0.15f),
    
    scrim = Color.Black.copy(alpha = 0.7f),
    
    inverseSurface = RixyColors.Surface,
    inverseOnSurface = RixyColors.TextPrimary,
    inversePrimary = RixyColors.Brand
)

// ============================================================================
// Composition Locals for extended theme
// ============================================================================

val LocalRixyColorScheme = staticCompositionLocalOf<RixyColorScheme> {
    error("No RixyColorScheme provided")
}

// ============================================================================
// Theme Composable
// ============================================================================

@Composable
fun RixyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val rixyColorScheme = when {
        darkTheme -> RixyColorScheme.Dark()
        else -> RixyColorScheme.Light()
    }
    
    CompositionLocalProvider(
        LocalRixyColorScheme provides rixyColorScheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = RixyMaterialTypography,
            shapes = RixyMaterialShapes,
            content = content
        )
    }
}

// ============================================================================
// Theme accessors
// ============================================================================

object RixyTheme {
    val colors: RixyColorScheme
        @Composable
        get() = LocalRixyColorScheme.current
    
    val typography = RixyTypography
    val shapes = RixyShapes
    val spacing = RixySpacing
    val dimensions = RixyDimensions
}

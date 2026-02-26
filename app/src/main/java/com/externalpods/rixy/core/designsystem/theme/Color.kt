package com.externalpods.rixy.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Rixy Design System v4.0 — Color Tokens
// Based on Rixy Design System (formerly MUUK)
// ============================================================================

object RixyColors {

    // Core Palette
    val Structure = Color(0xFF1D1D1F)
    val Brand = Color(0xFFE61E4D)
    val BrandLight = Color(0xFFFFE4EC)  // Light brand background
    val Action = Color(0xFF06B6D4)
    val Monetization = Color(0xFFFF9F1C)
    val Community = Color(0xFF7C9A92)

    // White & Black
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)

    // Neutrals (Light Mode))
    val Background = Color(0xFFF7F7F7)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFEBEBEB)
    val Border = Color(0xFFE5E7EB)
    val TextPrimary = Color(0xFF111827)
    val TextSecondary = Color(0xFF6B7280)
    val TextTertiary = Color(0xFF9CA3AF)

    // Dark Mode
    val BackgroundDark = Color(0xFF0F172A)
    val SurfaceDark = Color(0xFF1E293B)
    val BorderDark = Color(0xFF94A3B8)
    val TextPrimaryDark = Color(0xFFF8FAFC)
    val TextSecondaryDark = Color(0xFFCBD5E1)

    // Status Colors
    val Success = Color(0xFF22C55E)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)

    // Type Colors
    val Product = Color(0xFF3B82F6)      // Blue
    val Service = Color(0xFFA855F7)      // Purple
    val Event = Color(0xFFEC4899)        // Pink

    // Listing Status Colors
    val StatusDraft = Color(0xFF9CA3AF)
    val StatusPending = Color(0xFFF59E0B)
    val StatusPublished = Color(0xFF22C55E)
    val StatusRejected = Color(0xFFEF4444)
    val StatusSuspended = Color(0xFF6B7280)

    // Featured/Slot Status
    val SlotActive = Color(0xFF22C55E)
    val SlotPending = Color(0xFFF59E0B)
    val SlotExpired = Color(0xFF9CA3AF)
    val SlotPaused = Color(0xFF6B7280)

    // Opacity variations
    fun Brand(alpha: Float) = Brand.copy(alpha = alpha)
    fun Structure(alpha: Float) = Structure.copy(alpha = alpha)
    fun TextPrimary(alpha: Float) = TextPrimary.copy(alpha = alpha)
    fun TextSecondary(alpha: Float) = TextSecondary.copy(alpha = alpha)
}

// ============================================================================
// Material 3 Color Scheme Extensions
// ============================================================================

sealed class RixyColorScheme {
    abstract val background: Color
    abstract val surface: Color
    abstract val border: Color
    abstract val textPrimary: Color
    abstract val textSecondary: Color
    abstract val textTertiary: Color
    abstract val primary: Color
    abstract val secondary: Color
    abstract val accent: Color
    abstract val error: Color
    abstract val success: Color
    abstract val warning: Color
    abstract val info: Color
    abstract val onPrimary: Color
    abstract val onSecondary: Color
    abstract val onSurface: Color
    abstract val onBackground: Color
    
    data class Light(
        override val background: Color = RixyColors.Background,
        override val surface: Color = RixyColors.Surface,
        override val border: Color = RixyColors.Border,
        override val textPrimary: Color = RixyColors.TextPrimary,
        override val textSecondary: Color = RixyColors.TextSecondary,
        override val textTertiary: Color = RixyColors.TextTertiary,
        override val primary: Color = RixyColors.Brand,
        override val secondary: Color = RixyColors.Structure,
        override val accent: Color = RixyColors.Action,
        override val error: Color = RixyColors.Error,
        override val success: Color = RixyColors.Success,
        override val warning: Color = RixyColors.Warning,
        override val info: Color = RixyColors.Info,
        override val onPrimary: Color = Color.White,
        override val onSecondary: Color = Color.White,
        override val onSurface: Color = RixyColors.TextPrimary,
        override val onBackground: Color = RixyColors.TextPrimary
    ) : RixyColorScheme()
    
    data class Dark(
        override val background: Color = RixyColors.BackgroundDark,
        override val surface: Color = RixyColors.SurfaceDark,
        override val border: Color = RixyColors.BorderDark.copy(alpha = 0.25f),
        override val textPrimary: Color = RixyColors.TextPrimaryDark,
        override val textSecondary: Color = RixyColors.TextSecondaryDark,
        override val textTertiary: Color = RixyColors.TextSecondaryDark.copy(alpha = 0.6f),
        override val primary: Color = RixyColors.Brand,
        override val secondary: Color = RixyColors.Structure,
        override val accent: Color = RixyColors.Action,
        override val error: Color = RixyColors.Error,
        override val success: Color = RixyColors.Success,
        override val warning: Color = RixyColors.Warning,
        override val info: Color = RixyColors.Info,
        override val onPrimary: Color = Color.White,
        override val onSecondary: Color = Color.White,
        override val onSurface: Color = RixyColors.TextPrimaryDark,
        override val onBackground: Color = RixyColors.TextPrimaryDark
    ) : RixyColorScheme()
}

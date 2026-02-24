package com.externalpods.rixy.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// Rixy Design System v4.0 — Typography
// Font: Inter (system default as fallback)
// ============================================================================

object RixyTypography {
    
    // Font Family - Uses system font (Inter equivalent)
    val DefaultFontFamily: FontFamily = FontFamily.Default
    
    // Display Styles
    val H1 = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    )
    
    val H2 = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.02).sp
    )
    
    val H3 = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.01).sp
    )
    
    val H4 = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    )
    
    // Body Styles
    val BodyLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    val BodyMedium = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    val Body = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    // Subtext / Secondary
    val Subtext = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    val Caption = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    
    val CaptionSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
    
    // Button Styles
    val ButtonLarge = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    val Button = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    val ButtonSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    
    // Label Styles
    val Label = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    // Price/Amount
    val Price = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
    
    val PriceSmall = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

// ============================================================================
// Material 3 Typography
// ============================================================================

val RixyMaterialTypography = Typography(
    displayLarge = RixyTypography.H1,
    displayMedium = RixyTypography.H2,
    displaySmall = RixyTypography.H3,
    headlineLarge = RixyTypography.H3,
    headlineMedium = RixyTypography.H4,
    headlineSmall = RixyTypography.H4.copy(fontSize = 18.sp),
    titleLarge = RixyTypography.H4,
    titleMedium = RixyTypography.BodyMedium,
    titleSmall = RixyTypography.Body,
    bodyLarge = RixyTypography.BodyLarge,
    bodyMedium = RixyTypography.Body,
    bodySmall = RixyTypography.Subtext,
    labelLarge = RixyTypography.Button,
    labelMedium = RixyTypography.Label,
    labelSmall = RixyTypography.Caption
)

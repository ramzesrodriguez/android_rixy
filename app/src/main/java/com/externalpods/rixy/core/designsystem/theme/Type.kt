package com.externalpods.rixy.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.externalpods.rixy.R

// ============================================================================
// Rixy Design System v4.0 — Typography
// Font: Inter (matches iOS Design System)
// ============================================================================

object RixyTypography {
    
    // ============================================================================
    // Font Family - Inter with system fallback
    // ============================================================================
    
    val InterFontFamily: FontFamily = try {
        FontFamily(
            Font(R.font.inter_regular, FontWeight.Normal),
            Font(R.font.inter_medium, FontWeight.Medium),
            Font(R.font.inter_semibold, FontWeight.SemiBold),
            Font(R.font.inter_bold, FontWeight.Bold)
        )
    } catch (e: Exception) {
        // Fallback to system font if Inter not available
        FontFamily.Default
    }
    
    // Legacy alias for compatibility
    val DefaultFontFamily: FontFamily = InterFontFamily
    
    // ============================================================================
    // Display Styles (Headings)
    // Matches iOS: .DS.h1, .DS.h2
    // ============================================================================
    
    /**
     * H1 - Large display text
     * iOS: Font.custom("Inter-Bold", size: 34).leading(.tight)
     * Use: City names in hero, major screen titles
     */
    val H1 = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    )
    
    /**
     * H2 - Section headers
     * iOS: Font.custom("Inter-Bold", size: 28).leading(.tight)
     * Use: Section titles like "Explorar", "Destacados"
     */
    val H2 = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.02).sp
    )
    
    /**
     * H3 - Subsection headers
     * iOS equivalent: Semibold 24
     * Use: Card titles, modal headers
     */
    val H3 = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.01).sp
    )
    
    /**
     * H4 - Small headers
     * iOS equivalent: Semibold 20
     * Use: List headers, small card titles
     */
    val H4 = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    )
    
    // ============================================================================
    // Body Styles
    // Matches iOS: .DS.body, .DS.bodyMedium
    // ============================================================================
    
    /**
     * Body Large - Primary body text
     * iOS: Font.custom("Inter-Regular", size: 16)
     * Use: Main content, descriptions
     */
    val BodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    /**
     * Body Medium - Emphasized body text
     * iOS: Font.custom("Inter-Medium", size: 16)
     * Use: Emphasized content, labels
     */
    val BodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    /**
     * Body - Default body text
     * iOS: Font.custom("Inter-Regular", size: 16) - same as BodyLarge
     * Use: General text, card content
     */
    val Body = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    // ============================================================================
    // Secondary Text Styles
    // Matches iOS: .DS.subtext
    // ============================================================================
    
    /**
     * Subtext - Secondary information
     * iOS: Font.custom("Inter-Regular", size: 14)
     * Use: Captions, metadata, hints
     */
    val Subtext = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    /**
     * Caption - Small labels
     * iOS: Font.custom("Inter-SemiBold", size: 12)
     * Use: Badges, timestamps, small labels
     */
    val Caption = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    
    /**
     * Caption Small - Very small text
     * Use: Fine print, technical details
     */
    val CaptionSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
    
    // ============================================================================
    // Button Styles
    // Matches iOS: .DS.button
    // ============================================================================
    
    /**
     * Button Large - Primary actions
     * Use: Main CTA buttons
     */
    val ButtonLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    /**
     * Button - Standard buttons
     * iOS: Font.custom("Inter-Medium", size: 14)
     * Use: Default button text
     */
    val Button = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    /**
     * Button Small - Compact buttons
     * Use: Small actions, icon buttons with text
     */
    val ButtonSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    
    // ============================================================================
    // Specialized Styles
    // ============================================================================
    
    /**
     * Label - UI Labels
     * Use: Form labels, section labels
     */
    val Label = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    /**
     * Price - Price displays
     * Use: Product prices, amounts
     */
    val Price = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
    
    /**
     * Price Small - Small price displays
     * Use: Prices in compact cards
     */
    val PriceSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    // ============================================================================
    // iOS Compatibility Aliases
    // Matches iOS SF Font naming conventions
    // ============================================================================
    
    /** Title2 - iOS style large title */
    val Title2 = H2
    
    /** Title3 - iOS style title */
    val Title3 = H3
    
    /** Body Small - iOS style body small */
    val BodySmall = Subtext
}

// ============================================================================
// Material 3 Typography Mapping
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

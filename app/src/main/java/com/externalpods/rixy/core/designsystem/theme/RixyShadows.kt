package com.externalpods.rixy.core.designsystem.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rixy Design System v4.0 — Shadow Tokens
 * Replicates iOS-style soft shadows with blur
 */
object RixyShadows {
    
    /**
     * Standard card shadow - used in DSCard, ListingCard
     * iOS: .shadow(color: .black.opacity(0.08), radius: 9, x: 0, y: 6)
     */
    val Card = ShadowStyle(
        color = Color.Black.copy(alpha = 0.08f),
        offsetX = 0.dp,
        offsetY = 6.dp,
        blurRadius = 9.dp,
        spreadRadius = 0.dp
    )
    
    /**
     * Modal/Bottom sheet shadow
     * iOS: .shadow(color: .black.opacity(0.12), radius: 14, x: 0, y: 10)
     */
    val Modal = ShadowStyle(
        color = Color.Black.copy(alpha = 0.12f),
        offsetX = 0.dp,
        offsetY = 10.dp,
        blurRadius = 14.dp,
        spreadRadius = 0.dp
    )
    
    /**
     * Elevated card shadow - for hover/raised states
     */
    val Elevated = ShadowStyle(
        color = Color.Black.copy(alpha = 0.12f),
        offsetX = 0.dp,
        offsetY = 8.dp,
        blurRadius = 16.dp,
        spreadRadius = 0.dp
    )
    
    /**
     * Pressed state shadow - reduced height
     */
    val Pressed = ShadowStyle(
        color = Color.Black.copy(alpha = 0.04f),
        offsetX = 0.dp,
        offsetY = 2.dp,
        blurRadius = 4.dp,
        spreadRadius = 0.dp
    )
    
    /**
     * Subtle shadow for small elements
     */
    val Subtle = ShadowStyle(
        color = Color.Black.copy(alpha = 0.06f),
        offsetX = 0.dp,
        offsetY = 2.dp,
        blurRadius = 8.dp,
        spreadRadius = 0.dp
    )
    
    /**
     * Focus glow shadow - used in text fields
     * iOS: Color.DS.brand.opacity(0.18)
     */
    fun focusGlow(color: Color = RixyColors.Brand) = ShadowStyle(
        color = color.copy(alpha = 0.18f),
        offsetX = 0.dp,
        offsetY = 0.dp,
        blurRadius = 6.dp,
        spreadRadius = 0.dp
    )
    
    /**
     * Creates a shadow style with custom parameters
     */
    fun custom(
        color: Color = Color.Black,
        alpha: Float = 0.08f,
        offsetX: Dp = 0.dp,
        offsetY: Dp = 6.dp,
        blurRadius: Dp = 9.dp,
        spreadRadius: Dp = 0.dp
    ) = ShadowStyle(
        color = color.copy(alpha = alpha),
        offsetX = offsetX,
        offsetY = offsetY,
        blurRadius = blurRadius,
        spreadRadius = spreadRadius
    )
}

/**
 * Immutable data class representing a shadow configuration
 */
data class ShadowStyle(
    val color: Color,
    val offsetX: Dp,
    val offsetY: Dp,
    val blurRadius: Dp,
    val spreadRadius: Dp = 0.dp
) {
    val offset: Offset
        get() = Offset(offsetX.value, offsetY.value)
}

/**
 * Extension to convert a shadow style to a simpler elevation-based shadow
 * for components that need to work with Material3 elevation
 */
fun ShadowStyle.toElevationDp(): Dp = blurRadius / 2

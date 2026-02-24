package com.externalpods.rixy.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================================
// Rixy Design System v4.0 — Shapes
// ============================================================================

object RixyShapes {
    
    // Card radius: 16dp
    val Card = RoundedCornerShape(16.dp)
    
    // Button radius: 10dp
    val Button = RoundedCornerShape(10.dp)
    
    // Input radius: 8dp
    val Input = RoundedCornerShape(8.dp)
    
    // Small radius: 4dp (badges, chips)
    val Small = RoundedCornerShape(4.dp)
    
    // Medium radius: 12dp
    val Medium = RoundedCornerShape(12.dp)
    
    // Large radius: 20dp (bottom sheets, dialogs)
    val Large = RoundedCornerShape(20.dp)
    
    // Extra Large radius: 24dp
    val ExtraLarge = RoundedCornerShape(24.dp)
    
    // Pill / Capsule (fully rounded)
    val Pill = RoundedCornerShape(percent = 50)
    
    // Circle
    val Circle = RoundedCornerShape(percent = 50)
}

// ============================================================================
// Material 3 Shapes
// ============================================================================

val RixyMaterialShapes = Shapes(
    extraSmall = RixyShapes.Small,
    small = RixyShapes.Input,
    medium = RixyShapes.Button,
    large = RixyShapes.Card,
    extraLarge = RixyShapes.Large
)

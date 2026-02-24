package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.animations.RixyAnimations
import com.externalpods.rixy.core.designsystem.modifiers.cardShadow
import com.externalpods.rixy.core.designsystem.modifiers.iosShadow
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyShadows
import com.externalpods.rixy.core.designsystem.theme.ShadowStyle

/**
 * DSCard - iOS-style Card Component
 * 
 * Replicates the iOS DSCard.swift component with:
 * - Soft shadow (blur radius, not elevation)
 * - 24dp internal padding (not 16dp like Material3)
 * - Subtle border stroke
 * - Press animation with scale
 * 
 * @param modifier Modifier to be applied to the card
 * @param onClick Callback when card is clicked (null = not clickable)
 * @param shape Shape of the card (default: RoundedCornerShape(16.dp))
 * @param backgroundColor Background color (default: Surface)
 * @param contentColor Color of the content (text, etc.)
 * @param border Optional border stroke (default: 1dp Border)
 * @param shadow Shadow style (default: iOS card shadow)
 * @param padding Internal padding (default: 24dp like iOS)
 * @param pressScale Scale factor when pressed (default: 0.98f)
 * @param content Content of the card
 */
@Composable
fun DSCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = RixyColors.Surface,
    contentColor: Color = RixyColors.TextPrimary,
    border: BorderStroke? = BorderStroke(1.dp, RixyColors.Border),
    shadow: ShadowStyle = RixyShadows.Card,
    padding: Dp = 24.dp,
    pressScale: Float = 0.98f,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) pressScale else 1f,
        animationSpec = RixyAnimations.springPress(),
        label = "card_press_scale"
    )
    
    val cardModifier = modifier
        .scale(scale)
        .iosShadow(style = shadow, shape = shape, clip = true)
        .clip(shape)
        .background(backgroundColor)
        .then(
            if (border != null) {
                Modifier.border(border.width, border.brush, shape)
            } else Modifier
        )
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = RixyColors.Brand.copy(alpha = 0.1f)),
                    onClick = onClick
                )
            } else Modifier
        )
    
    Box(
        modifier = cardModifier,
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

/**
 * DSOutlinedCard - Card with border but no shadow
 * Use for: Subtle containers, outlined sections
 */
@Composable
fun DSOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = RixyColors.Surface,
    borderColor: Color = RixyColors.Border,
    borderWidth: Dp = 1.dp,
    padding: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    DSCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        backgroundColor = backgroundColor,
        border = BorderStroke(borderWidth, borderColor),
        shadow = RixyShadows.Pressed, // Minimal shadow
        padding = padding,
        content = content
    )
}

/**
 * DSElevatedCard - Card with elevated shadow
 * Use for: Emphasized content, modals, dropdowns
 */
@Composable
fun DSElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = RixyColors.Surface,
    padding: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    DSCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        backgroundColor = backgroundColor,
        border = null, // No border for elevated
        shadow = RixyShadows.Elevated,
        padding = padding,
        content = content
    )
}

/**
 * DSSurfaceCard - Flat card without shadow or border
 * Use for: Grouping content subtly
 */
@Composable
fun DSSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = RixyColors.Surface,
    padding: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    DSCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        backgroundColor = backgroundColor,
        border = null,
        shadow = RixyShadows.Pressed, // Very subtle
        padding = padding,
        content = content
    )
}

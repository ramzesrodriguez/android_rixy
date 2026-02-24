package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.animations.RixyAnimations
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSButton - iOS-style Button Component
 * 
 * Replicates DSButton.swift with:
 * - 44dp height for standard buttons (not 48dp Material3)
 * - 10dp border radius
 * - Scale 0.96x on press with spring animation
 * - Haptic feedback
 * 
 * Variants:
 * - PRIMARY: Brand background, white text
 * - SECONDARY: Structure background, white text  
 * - OUTLINE: Transparent with border
 * - GHOST: Transparent, structure text
 * - LINK: Brand text, no background
 * - DESTRUCTIVE: Error background
 */
enum class DSButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE,
    GHOST,
    LINK,
    DESTRUCTIVE
}

enum class DSButtonSize {
    SMALL,   // 36dp height, 12dp horizontal padding
    MEDIUM,  // 44dp height, 20dp horizontal padding
    LARGE,   // 44dp height, 32dp horizontal padding
    ICON     // 40dp x 40dp
}

@Composable
fun DSButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: DSButtonVariant = DSButtonVariant.PRIMARY,
    size: DSButtonSize = DSButtonSize.MEDIUM,
    icon: ImageVector? = null,
    iconPosition: DSIconPosition = DSIconPosition.START,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fillWidth: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // iOS-style press animation: scale to 0.96x
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isLoading) 0.96f else 1f,
        animationSpec = RixyAnimations.springPress(),
        label = "button_press_scale"
    )
    
    // Dimensions based on size
    val (height, hPadding, textStyle) = when (size) {
        DSButtonSize.SMALL -> Triple(36.dp, 12.dp, RixyTypography.Button)
        DSButtonSize.MEDIUM -> Triple(44.dp, 20.dp, RixyTypography.Button)
        DSButtonSize.LARGE -> Triple(44.dp, 32.dp, RixyTypography.ButtonLarge)
        DSButtonSize.ICON -> Triple(40.dp, 0.dp, RixyTypography.Button)
    }
    
    // Colors based on variant and state
    val (backgroundColor, contentColor, border) = getButtonColors(variant, enabled)
    
    Box(
        modifier = modifier
            .scale(scale)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .then(if (size == DSButtonSize.ICON) Modifier.size(height) else Modifier.height(height))
            .defaultMinSize(minWidth = if (size == DSButtonSize.ICON) height else 64.dp)
            .clip(RoundedCornerShape(10.dp)) // iOS radius
            .then(
                if (border != null && variant == DSButtonVariant.OUTLINE) {
                    Modifier.border(1.dp, border, RoundedCornerShape(10.dp))
                } else Modifier
            )
            .background(if (variant != DSButtonVariant.LINK) backgroundColor else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom handling
                enabled = enabled && !isLoading
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = hPadding),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null && iconPosition == DSIconPosition.START && size != DSButtonSize.ICON) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                if (size != DSButtonSize.ICON) {
                    Text(
                        text = title,
                        style = textStyle,
                        color = contentColor
                    )
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                if (icon != null && iconPosition == DSIconPosition.END && size != DSButtonSize.ICON) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getButtonColors(
    variant: DSButtonVariant,
    enabled: Boolean
): Triple<Color, Color, Color?> {
    if (!enabled) {
        return Triple(
            RixyColors.TextTertiary.copy(alpha = 0.2f),
            RixyColors.TextTertiary,
            null
        )
    }
    
    return when (variant) {
        DSButtonVariant.PRIMARY -> Triple(
            RixyColors.Brand,
            Color.White,
            null
        )
        DSButtonVariant.SECONDARY -> Triple(
            RixyColors.Structure,
            Color.White,
            null
        )
        DSButtonVariant.OUTLINE -> Triple(
            Color.Transparent,
            RixyColors.TextPrimary,
            RixyColors.Structure.copy(alpha = 0.2f)
        )
        DSButtonVariant.GHOST -> Triple(
            Color.Transparent,
            RixyColors.Structure,
            null
        )
        DSButtonVariant.LINK -> Triple(
            Color.Transparent,
            RixyColors.Brand,
            null
        )
        DSButtonVariant.DESTRUCTIVE -> Triple(
            RixyColors.Error,
            Color.White,
            null
        )
    }
}

enum class DSIconPosition {
    START,
    END
}

// Convenience variants for common use cases

@Composable
fun DSPrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: DSButtonSize = DSButtonSize.MEDIUM,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fillWidth: Boolean = false
) = DSButton(
    title = title,
    onClick = onClick,
    modifier = modifier,
    variant = DSButtonVariant.PRIMARY,
    size = size,
    icon = icon,
    enabled = enabled,
    isLoading = isLoading,
    fillWidth = fillWidth
)

@Composable
fun DSOutlineButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: DSButtonSize = DSButtonSize.MEDIUM,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fillWidth: Boolean = false
) = DSButton(
    title = title,
    onClick = onClick,
    modifier = modifier,
    variant = DSButtonVariant.OUTLINE,
    size = size,
    icon = icon,
    enabled = enabled,
    isLoading = isLoading,
    fillWidth = fillWidth
)

@Composable
fun DSLinkButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: DSButtonSize = DSButtonSize.MEDIUM,
    icon: ImageVector? = null,
    enabled: Boolean = true
) = DSButton(
    title = title,
    onClick = onClick,
    modifier = modifier,
    variant = DSButtonVariant.LINK,
    size = size,
    icon = icon,
    enabled = enabled
)

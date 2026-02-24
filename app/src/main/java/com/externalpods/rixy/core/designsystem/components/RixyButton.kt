package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyDimensions
import com.externalpods.rixy.core.designsystem.theme.RixyShapes
import com.externalpods.rixy.core.designsystem.theme.RixySpacing
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

enum class ButtonVariant {
    PRIMARY,      // Brand color
    SECONDARY,    // Structure color
    OUTLINE,      // Bordered
    OUTLINED,     // Alias for OUTLINE (deprecated naming)
    GHOST,        // No background
    DESTRUCTIVE,  // Error color
    MONETIZATION  // Monetization color
}

enum class ButtonSize {
    SMALL,        // 36dp height
    MEDIUM,       // 44dp height (default)
    LARGE,        // 48dp height
    ICON          // 40dp square
}

@Composable
fun RixyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.START,
    enabled: Boolean = true,
    loading: Boolean = false,
    isLoading: Boolean = false
) {
    val isActuallyLoading = loading || isLoading
    val height = when (size) {
        ButtonSize.SMALL -> RixyDimensions.ButtonSmallHeight
        ButtonSize.MEDIUM -> RixyDimensions.ButtonHeight
        ButtonSize.LARGE -> RixyDimensions.ButtonLargeHeight
        ButtonSize.ICON -> RixyDimensions.ButtonIconSize
    }
    
    val horizontalPadding = when (size) {
        ButtonSize.SMALL -> RixySpacing.Small
        ButtonSize.MEDIUM -> RixySpacing.Large
        ButtonSize.LARGE -> 32.dp
        ButtonSize.ICON -> 0.dp
    }
    
    val content: @Composable () -> Unit = {
        if (isActuallyLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor(variant),
                strokeWidth = 2.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null && iconPosition == IconPosition.START) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    if (size != ButtonSize.ICON) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                if (size != ButtonSize.ICON) {
                    Text(
                        text = text,
                        style = when (size) {
                            ButtonSize.LARGE -> RixyTypography.ButtonLarge
                            else -> RixyTypography.Button
                        }
                    )
                }
                if (icon != null && iconPosition == IconPosition.END) {
                    if (size != ButtonSize.ICON) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
    
    when (variant) {
        ButtonVariant.PRIMARY, ButtonVariant.SECONDARY, 
        ButtonVariant.DESTRUCTIVE, ButtonVariant.MONETIZATION -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isActuallyLoading,
                shape = RixyShapes.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor(variant),
                    contentColor = contentColor(variant),
                    disabledContainerColor = RixyColors.TextTertiary.copy(alpha = 0.2f),
                    disabledContentColor = RixyColors.TextTertiary
                ),
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            ) {
                content()
            }
        }
        ButtonVariant.OUTLINE, ButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isActuallyLoading,
                shape = RixyShapes.Button,
                border = BorderStroke(1.dp, RixyColors.Border),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = RixyColors.TextPrimary,
                    disabledContentColor = RixyColors.TextTertiary
                ),
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            ) {
                content()
            }
        }
        ButtonVariant.GHOST -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.height(height),
                enabled = enabled && !isActuallyLoading,
                shape = RixyShapes.Button,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = RixyColors.Brand,
                    disabledContentColor = RixyColors.TextTertiary
                ),
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun backgroundColor(variant: ButtonVariant): Color = when (variant) {
    ButtonVariant.PRIMARY -> RixyColors.Brand
    ButtonVariant.SECONDARY -> RixyColors.Structure
    ButtonVariant.DESTRUCTIVE -> RixyColors.Error
    ButtonVariant.MONETIZATION -> RixyColors.Monetization
    else -> MaterialTheme.colorScheme.primary
}

@Composable
private fun contentColor(variant: ButtonVariant): Color = Color.White

enum class IconPosition { START, END }

package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyShapes
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

enum class BadgeVariant {
    FEATURED,    // Brand color
    SPONSORED,   // Monetization color
    VERIFIED,    // Community color
    SECONDARY,   // Structure color
    OUTLINE,     // Border only
    SUCCESS,     // Success color
    WARNING,     // Warning color
    ERROR,       // Error color
    INFO         // Info color
}

@Composable
fun RixyBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.FEATURED
) {
    Box(
        modifier = modifier
            .clip(RixyShapes.Pill)
            .background(backgroundColor(variant))
            .then(
                if (variant == BadgeVariant.OUTLINE) {
                    Modifier.border(1.dp, RixyColors.Border, RixyShapes.Pill)
                } else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = RixyTypography.Caption,
            color = contentColor(variant),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RixyStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    val (bgColor, textColor) = when {
        !isActive -> Pair(RixyColors.TextTertiary.copy(alpha = 0.1f), RixyColors.TextTertiary)
        status.equals("active", ignoreCase = true) || 
        status.equals("published", ignoreCase = true) -> 
            Pair(RixyColors.Success.copy(alpha = 0.1f), RixyColors.Success)
        status.equals("pending", ignoreCase = true) -> 
            Pair(RixyColors.Warning.copy(alpha = 0.15f), RixyColors.Warning)
        status.equals("expired", ignoreCase = true) || 
        status.equals("draft", ignoreCase = true) -> 
            Pair(RixyColors.TextTertiary.copy(alpha = 0.1f), RixyColors.TextSecondary)
        status.equals("rejected", ignoreCase = true) || 
        status.equals("suspended", ignoreCase = true) -> 
            Pair(RixyColors.Error.copy(alpha = 0.1f), RixyColors.Error)
        else -> Pair(RixyColors.Brand.copy(alpha = 0.1f), RixyColors.Brand)
    }
    
    Box(
        modifier = modifier
            .clip(RixyShapes.Pill)
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            style = RixyTypography.Caption,
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
private fun backgroundColor(variant: BadgeVariant): Color = when (variant) {
    BadgeVariant.FEATURED -> RixyColors.Brand.copy(alpha = 0.1f)
    BadgeVariant.SPONSORED -> RixyColors.Monetization.copy(alpha = 0.15f)
    BadgeVariant.VERIFIED -> RixyColors.Community.copy(alpha = 0.15f)
    BadgeVariant.SECONDARY -> RixyColors.Structure.copy(alpha = 0.1f)
    BadgeVariant.OUTLINE -> Color.Transparent
    BadgeVariant.SUCCESS -> RixyColors.Success.copy(alpha = 0.1f)
    BadgeVariant.WARNING -> RixyColors.Warning.copy(alpha = 0.15f)
    BadgeVariant.ERROR -> RixyColors.Error.copy(alpha = 0.1f)
    BadgeVariant.INFO -> RixyColors.Info.copy(alpha = 0.1f)
}

@Composable
private fun contentColor(variant: BadgeVariant): Color = when (variant) {
    BadgeVariant.FEATURED -> RixyColors.Brand
    BadgeVariant.SPONSORED -> RixyColors.Monetization
    BadgeVariant.VERIFIED -> RixyColors.Community
    BadgeVariant.SECONDARY -> RixyColors.Structure
    BadgeVariant.OUTLINE -> RixyColors.TextPrimary
    BadgeVariant.SUCCESS -> RixyColors.Success
    BadgeVariant.WARNING -> RixyColors.Warning
    BadgeVariant.ERROR -> RixyColors.Error
    BadgeVariant.INFO -> RixyColors.Info
}

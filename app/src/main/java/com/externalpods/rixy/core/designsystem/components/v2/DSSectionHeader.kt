package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSSectionHeader - iOS-style Section Header
 * 
 * Replicates SectionHeader.swift with:
 * - Title in H2 style (22sp bold)
 * - Optional subtitle in subtext style
 * - Optional "Ver más" action
 * - Proper alignment to baseline
 * 
 * @param title Section title
 * @param modifier Modifier to apply
 * @param subtitle Optional subtitle text
 * @param actionText Text for the action button (default: "Ver más")
 * @param onActionClick Callback when action is clicked (null = no action)
 * @param showArrow Whether to show arrow icon next to action
 */
@Composable
fun DSSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionText: String = "Ver más",
    onActionClick: (() -> Unit)? = null,
    showArrow: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top // Align to top like iOS
    ) {
        // Title and subtitle column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Spacing.xs
        ) {
            Text(
                text = title,
                style = RixyTypography.H2, // 22sp bold like iOS
                color = RixyColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            subtitle?.let {
                Text(
                    text = it,
                    style = RixyTypography.Subtext, // 14sp regular
                    color = RixyColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // Action button
        onActionClick?.let { onClick ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = actionText,
                    style = RixyTypography.Caption, // 12sp semibold
                    color = RixyColors.Brand
                )
                
                if (showArrow) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = RixyColors.Brand,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            // No explicit size, let it use default (24dp)
                    )
                }
            }
        }
    }
}

/**
 * Simple section header with just title
 * Use when you don't need action or subtitle
 */
@Composable
fun DSSectionHeaderSimple(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = RixyTypography.H2,
        color = RixyColors.TextPrimary,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

/**
 * Subsection header - smaller, for nested sections
 */
@Composable
fun DSSubsectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = RixyColors.TextSecondary
) {
    Text(
        text = title,
        style = RixyTypography.BodyMedium,
        color = color,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/**
 * Section header with custom action content
 */
@Composable
fun DSSectionHeaderCustom(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = RixyTypography.H2,
                color = RixyColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            subtitle?.let {
                Text(
                    text = it,
                    style = RixyTypography.Subtext,
                    color = RixyColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        action?.invoke()
    }
}

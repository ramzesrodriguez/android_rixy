package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

@Immutable
data class DSParallaxToolbarState(
    val alpha: Float,
    val isCollapsed: Boolean
)

@Composable
fun rememberDSParallaxToolbarState(
    listState: LazyListState,
    headerHeightPx: Float,
    collapseThreshold: Float = 0.9f
): DSParallaxToolbarState {
    val state by remember(listState, headerHeightPx, collapseThreshold) {
        derivedStateOf {
            val firstItemVisible = listState.firstVisibleItemIndex == 0
            val firstItemOffset = listState.firstVisibleItemScrollOffset.toFloat()
            val alpha = if (firstItemVisible && headerHeightPx > 0f) {
                (firstItemOffset / headerHeightPx).coerceIn(0f, 1f)
            } else {
                1f
            }
            DSParallaxToolbarState(
                alpha = alpha,
                isCollapsed = alpha > collapseThreshold
            )
        }
    }
    return state
}

@Composable
fun DSParallaxToolbar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float,
    isCollapsed: Boolean,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    actions: @Composable RowScope.(iconTint: Color, iconContainerColor: Color) -> Unit = { _, _ -> }
) {
    val backgroundColor = RixyColors.Surface.copy(alpha = alpha)
    val iconContainerColor = if (isCollapsed) Color.Transparent else Color.Black.copy(alpha = 0.5f)
    val iconTint = if (isCollapsed) RixyColors.TextPrimary else Color.White
    val shadowElevation = if (isCollapsed) 2.dp else 0.dp

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = shadowElevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            DSParallaxToolbarIconButton(
                icon = navigationIcon,
                contentDescription = "Back",
                onClick = onBackClick,
                iconTint = iconTint,
                containerColor = iconContainerColor,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            if (isCollapsed) {
                Text(
                    text = title,
                    style = RixyTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = RixyColors.TextPrimary
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions(iconTint, iconContainerColor)
            }
        }
    }
}

@Composable
fun DSParallaxToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = RixyColors.TextPrimary,
    containerColor: Color = Color.Transparent
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(color = containerColor, shape = CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint
        )
    }
}

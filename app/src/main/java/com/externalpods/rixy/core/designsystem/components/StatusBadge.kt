package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * Simple active/inactive badge
 */
@Composable
fun ActiveBadge(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = if (isActive) {
        Triple(
            RixyColors.Success.copy(alpha = 0.15f),
            RixyColors.Success,
            "Activo"
        )
    } else {
        Triple(
            RixyColors.TextTertiary.copy(alpha = 0.15f),
            RixyColors.TextSecondary,
            "Inactivo"
        )
    }
    
    Text(
        text = label,
        style = RixyTypography.Caption,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

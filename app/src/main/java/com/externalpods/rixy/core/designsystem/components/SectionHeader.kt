package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * Section header with title and optional "See All" action
 */
@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        onSeeAllClick?.let { onClick ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Text(
                    text = "Ver todo",
                    style = RixyTypography.Body,
                    color = RixyColors.Brand
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = RixyColors.Brand,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

/**
 * Simple section header without action
 */
@Composable
fun SectionHeaderSimple(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = RixyTypography.H4,
        color = RixyColors.TextPrimary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/**
 * Subsection header with smaller text
 */
@Composable
fun SubsectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = RixyTypography.BodyMedium,
        color = RixyColors.TextSecondary,
        modifier = modifier.padding(vertical = 4.dp)
    )
}

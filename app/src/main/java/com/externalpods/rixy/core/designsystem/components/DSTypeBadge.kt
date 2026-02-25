package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSTypeBadge - iOS-style Type Badge
 * 
 * Replicates TypeBadge from ListingCard.swift:
 * - Capsule/rounded shape with icon emoji + text
 * - Semi-transparent background
 * - White text
 * - Used on listing images
 * 
 * Types:
 * - PRODUCT: Blue background, "🛍️ Producto"
 * - SERVICE: Purple background, "🔧 Servicio"  
 * - EVENT: Pink/Orange background, "🎉 Evento"
 */
enum class ListingType {
    PRODUCT,
    SERVICE,
    EVENT
}

@Composable
fun DSTypeBadge(
    type: ListingType,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val (icon, label, color) = when (type) {
        ListingType.PRODUCT -> Triple("🛍️", "Producto", Color(0xFF3B82F6))
        ListingType.SERVICE -> Triple("🔧", "Servicio", Color(0xFFA855F7))
        ListingType.EVENT -> Triple("🎉", "Evento", Color(0xFFEC4899))
    }
    
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp) // iOS uses 4dp, not pill
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showIcon) {
            Text(
                text = icon,
                style = RixyTypography.Caption.copy(
                    fontSize = 10.sp // Slightly smaller for emoji
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            style = RixyTypography.Caption,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Small type badge for compact cards
 * Just the text with color, no background
 */
@Composable
fun DSTypeBadgeSmall(
    type: ListingType,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (type) {
        ListingType.PRODUCT -> Pair(RixyColors.Product, "Producto")
        ListingType.SERVICE -> Pair(RixyColors.Service, "Servicio")
        ListingType.EVENT -> Pair(RixyColors.Event, "Evento")
    }
    
    Text(
        text = label,
        style = RixyTypography.CaptionSmall,
        color = color,
        modifier = modifier
    )
}

/**
 * Capsule-style badge for hero cards
 * Black semi-transparent background like iOS
 */
@Composable
fun DSTypeBadgeCapsule(
    type: ListingType,
    modifier: Modifier = Modifier
) {
    val (icon, label) = when (type) {
        ListingType.PRODUCT -> Pair("🛍️", "Producto")
        ListingType.SERVICE -> Pair("🔧", "Servicio")
        ListingType.EVENT -> Pair("🎉", "Evento")
    }
    
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(percent = 50) // Full pill
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = RixyTypography.Caption,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = RixyTypography.Caption,
            color = Color.White
        )
    }
}

/**
 * Status badge for listings (draft, pending, published, etc.)
 * Matches iOS StatusBadge
 */
@Composable
fun DSStatusBadge(
    status: ListingStatus,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (status) {
        ListingStatus.DRAFT -> Pair("Borrador", RixyColors.TextTertiary)
        ListingStatus.PENDING -> Pair("En revisión", RixyColors.Warning)
        ListingStatus.PUBLISHED -> Pair("Publicado", RixyColors.Success)
        ListingStatus.REJECTED -> Pair("Rechazado", RixyColors.Error)
        ListingStatus.SUSPENDED -> Pair("Suspendido", RixyColors.TextSecondary)
    }
    
    Text(
        text = label,
        style = RixyTypography.Caption,
        color = color,
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

enum class ListingStatus {
    DRAFT,
    PENDING,
    PUBLISHED,
    REJECTED,
    SUSPENDED
}



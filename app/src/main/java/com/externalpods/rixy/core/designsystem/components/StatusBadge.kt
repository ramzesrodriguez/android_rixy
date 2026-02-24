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
import com.externalpods.rixy.core.model.ListingStatus
import com.externalpods.rixy.core.model.ModerationStatus
import com.externalpods.rixy.core.model.PaymentStatus

/**
 * Status badge component for various status types
 */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = RixyTypography.Caption,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

/**
 * Listing status badge
 */
@Composable
fun ListingStatusBadge(
    status: ListingStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        ListingStatus.PUBLISHED -> Triple(
            RixyColors.Success.copy(alpha = 0.15f),
            RixyColors.Success,
            "Publicado"
        )
        ListingStatus.DRAFT -> Triple(
            RixyColors.TextTertiary.copy(alpha = 0.15f),
            RixyColors.TextSecondary,
            "Borrador"
        )
        ListingStatus.PENDING_REVIEW -> Triple(
            RixyColors.Warning.copy(alpha = 0.15f),
            RixyColors.Warning,
            "Pendiente"
        )
        ListingStatus.SCHEDULED -> Triple(
            RixyColors.Info.copy(alpha = 0.15f),
            RixyColors.Info,
            "Programado"
        )
        ListingStatus.EXPIRED -> Triple(
            RixyColors.Error.copy(alpha = 0.15f),
            RixyColors.Error,
            "Expirado"
        )
        else -> Triple(
            RixyColors.SurfaceVariant,
            RixyColors.TextSecondary,
            "Desconocido"
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

/**
 * Moderation status badge
 */
@Composable
fun ModerationStatusBadge(
    status: ModerationStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        ModerationStatus.PENDING -> Triple(
            RixyColors.Warning.copy(alpha = 0.15f),
            RixyColors.Warning,
            "Pendiente"
        )
        ModerationStatus.APPROVED -> Triple(
            RixyColors.Success.copy(alpha = 0.15f),
            RixyColors.Success,
            "Aprobado"
        )
        ModerationStatus.REJECTED -> Triple(
            RixyColors.Error.copy(alpha = 0.15f),
            RixyColors.Error,
            "Rechazado"
        )
        ModerationStatus.SUSPENDED -> Triple(
            RixyColors.Structure.copy(alpha = 0.15f),
            RixyColors.Structure,
            "Suspendido"
        )
        ModerationStatus.FLAGGED -> Triple(
            RixyColors.Brand.copy(alpha = 0.15f),
            RixyColors.Brand,
            "Marcado"
        )
        else -> Triple(
            RixyColors.SurfaceVariant,
            RixyColors.TextSecondary,
            "Desconocido"
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

/**
 * Payment status badge
 */
@Composable
fun PaymentStatusBadge(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status) {
        PaymentStatus.PENDING -> Triple(
            RixyColors.Warning.copy(alpha = 0.15f),
            RixyColors.Warning,
            "Pendiente"
        )
        PaymentStatus.COMPLETED -> Triple(
            RixyColors.Success.copy(alpha = 0.15f),
            RixyColors.Success,
            "Completado"
        )
        PaymentStatus.FAILED -> Triple(
            RixyColors.Error.copy(alpha = 0.15f),
            RixyColors.Error,
            "Fallido"
        )
        PaymentStatus.REFUNDED -> Triple(
            RixyColors.Info.copy(alpha = 0.15f),
            RixyColors.Info,
            "Reembolsado"
        )
        PaymentStatus.CANCELLED -> Triple(
            RixyColors.TextTertiary.copy(alpha = 0.15f),
            RixyColors.TextSecondary,
            "Cancelado"
        )
        else -> Triple(
            RixyColors.SurfaceVariant,
            RixyColors.TextSecondary,
            "Desconocido"
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

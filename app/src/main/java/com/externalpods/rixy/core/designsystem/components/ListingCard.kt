package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.ListingType

/**
 * Listing card with 4:3 image ratio
 * Displays title, price, type badge, and business name
 */
@Composable
fun ListingCard(
    title: String,
    imageUrl: String?,
    price: String?,
    type: ListingType,
    businessName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Column {
            // Image with type badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                
                // Type badge
                ListingTypeBadge(
                    type = type,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = RixyTypography.BodyMedium,
                    color = RixyColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Price
                price?.let {
                    Text(
                        text = it,
                        style = RixyTypography.Price,
                        color = RixyColors.Brand
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Business name
                businessName?.let {
                    Text(
                        text = it,
                        style = RixyTypography.Caption,
                        color = RixyColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Compact horizontal listing card
 */
@Composable
fun ListingCardHorizontal(
    title: String,
    imageUrl: String?,
    price: String?,
    type: ListingType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RixyColors.SurfaceVariant)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ListingTypeBadgeSmall(type = type)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = title,
                    style = RixyTypography.BodyMedium,
                    color = RixyColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                price?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = RixyTypography.PriceSmall,
                        color = RixyColors.Brand
                    )
                }
            }
        }
    }
}

/**
 * Type badge for listings
 */
@Composable
private fun ListingTypeBadge(
    type: ListingType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (type) {
        ListingType.PRODUCT -> Triple(
            RixyColors.Brand.copy(alpha = 0.9f),
            Color.White,
            "Producto"
        )
        ListingType.SERVICE -> Triple(
            RixyColors.Action.copy(alpha = 0.9f),
            Color.White,
            "Servicio"
        )
        ListingType.EVENT -> Triple(
            RixyColors.Warning.copy(alpha = 0.9f),
            RixyColors.Structure,
            "Evento"
        )
        else -> Triple(
            RixyColors.Surface.copy(alpha = 0.9f),
            RixyColors.TextPrimary,
            "Otro"
        )
    }
    
    Text(
        text = label,
        style = RixyTypography.Caption,
        color = textColor,
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun ListingTypeBadgeSmall(
    type: ListingType,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (type) {
        ListingType.PRODUCT -> Pair(RixyColors.Brand, "Producto")
        ListingType.SERVICE -> Pair(RixyColors.Action, "Servicio")
        ListingType.EVENT -> Pair(RixyColors.Warning, "Evento")
        else -> Pair(RixyColors.TextSecondary, "Otro")
    }
    
    Text(
        text = label,
        style = RixyTypography.CaptionSmall,
        color = color,
        modifier = modifier
    )
}

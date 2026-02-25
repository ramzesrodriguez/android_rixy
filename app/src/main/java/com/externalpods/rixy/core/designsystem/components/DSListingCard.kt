package com.externalpods.rixy.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// Using DSAsyncImage instead of coil directly
import com.externalpods.rixy.core.designsystem.animations.RixyAnimations
import com.externalpods.rixy.core.designsystem.modifiers.cardShadow
import com.externalpods.rixy.core.designsystem.modifiers.iosShadow
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyShadows
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSListingCard - iOS-style Listing Card (Horizontal/Carousel)
 * 
 * Replicates ListingCard.swift with:
 * - Fixed width: 260dp
 * - Aspect ratio 4:3 for image (195dp height)
 * - Type badge on image (top-start)
 * - Heart button on image (top-end) with shadow
 * - Fixed height info section (100dp)
 * - Title: 38dp height, 2 lines max
 * - Business: 14dp height, 1 line
 * - Price: 16dp height, brand color
 * 
 * @param title Listing title
 * @param imageUrl URL of the first image
 * @param priceFormatted Price string (already formatted with currency)
 * @param type Listing type (PRODUCT, SERVICE, EVENT)
 * @param businessName Business name
 * @param isFavorite Current favorite state (null = manage locally)
 * @param onFavoriteClick Callback when heart is clicked
 * @param onCardClick Callback when card is clicked
 * @param useFixedWidth Whether to keep the default iOS fixed width (260dp)
 * @param modifier Modifier to apply
 */
@Composable
fun DSListingCard(
    title: String,
    imageUrl: String?,
    priceFormatted: String?,
    type: ListingType,
    businessName: String?,
    isFavorite: Boolean? = null,
    onFavoriteClick: (() -> Unit)? = null,
    onCardClick: () -> Unit,
    useFixedWidth: Boolean = true,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var localIsFavorite by remember { mutableStateOf(false) }
    val resolvedIsFavorite = isFavorite ?: localIsFavorite
    
    // Heart animation
    val heartScale by animateFloatAsState(
        targetValue = if (resolvedIsFavorite) 1.2f else 1f,
        animationSpec = RixyAnimations.springBouncy(),
        label = "heart_scale"
    )
    
    // Fixed dimensions from iOS
    val cardWidth: Dp = 260.dp
    val imageHeight = 195.dp // 4:3 aspect ratio of 260dp
    val infoHeight = 100.dp
    
    Column(
        modifier = modifier
            .then(if (useFixedWidth) Modifier.width(cardWidth) else Modifier.fillMaxWidth())
            .cardShadow(elevation = 4.dp, borderRadius = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(RixyColors.Surface)
            .clickable(onClick = onCardClick)
    ) {
        // Image section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
        ) {
            // Image
            DSAsyncImage(
                imageUrl = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Type badge (top-start)
            DSTypeBadge(
                type = type,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
            
            // Heart button (top-end)
            onFavoriteClick?.let { onClick ->
                IconButton(
                    onClick = {
                        if (isFavorite == null) {
                            localIsFavorite = !localIsFavorite
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = if (resolvedIsFavorite) 
                            Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (resolvedIsFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (resolvedIsFavorite) RixyColors.Error else Color.White,
                        modifier = Modifier
                            .scale(heartScale)
                            // Subtle shadow for visibility on any image
                            .iosShadow(
                                style = RixyShadows.custom(
                                    color = Color.Black,
                                    alpha = 0.3f,
                                    blurRadius = 4.dp,
                                    offsetY = 2.dp
                                )
                            )
                    )
                }
            }
        }
        
        // Info section - FIXED HEIGHT like iOS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoHeight)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Title - 38dp height, 2 lines max
            Text(
                text = title,
                style = RixyTypography.Body,
                color = RixyColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(38.dp)
            )
            
            // Business name - 14dp height, always show space if null
            Text(
                text = businessName ?: " ",
                style = RixyTypography.Caption,
                color = RixyColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(14.dp)
            )
            
            // Price - 16dp height, always show space if null
            Text(
                text = priceFormatted ?: " ",
                style = RixyTypography.Caption.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = RixyColors.Brand,
                maxLines = 1,
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

/**
 * DSListingCardCompact - Horizontal list row
 * Use for: Feed section, recent listings
 * 
 * Replicates FeedListingRow from iOS
 */
@Composable
fun DSListingCardCompact(
    title: String,
    imageUrl: String?,
    priceFormatted: String?,
    type: ListingType,
    businessName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .iosShadow(style = RixyShadows.Card, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(RixyColors.Surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail - 80dp x 80dp
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(RixyColors.SurfaceVariant)
        ) {
            DSAsyncImage(
                imageUrl = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Type badge small
            DSTypeBadgeSmall(type = type)
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Title
            Text(
                text = title,
                style = RixyTypography.Body,
                color = RixyColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Price
            priceFormatted?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = RixyTypography.Caption.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = RixyColors.Brand,
                    maxLines = 1
                )
            }
        }
        
        // Chevron (optional)
        // Icon(
        //     imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        //     contentDescription = null,
        //     tint = RixyColors.TextSecondary.copy(alpha = 0.6f)
        // )
    }
}

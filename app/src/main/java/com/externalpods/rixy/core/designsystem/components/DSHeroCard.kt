package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// Using DSAsyncImage instead of coil directly
import com.externalpods.rixy.core.designsystem.modifiers.cardShadow
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSHeroSlotCard - Full-width hero card for featured listings
 * 
 * Replicates HeroSlotCard from CityHomeView.swift:
 * - Full width with 16dp horizontal padding
 * - Image 200dp height with capsule type badge
 * - Fixed height info section (90dp)
 * - Title: 20sp semibold, 1 line
 * - Business: 14sp subtext, 1 line
 * - Price: Brand color, semibold
 */
@Composable
fun DSHeroSlotCard(
    title: String,
    imageUrl: String?,
    priceFormatted: String?,
    type: ListingType,
    businessName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageHeight = 200.dp
    val infoHeight = 90.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(elevation = 4.dp, borderRadius = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(RixyColors.Surface)
            .clickable(onClick = onClick)
    ) {
        // Image container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
        ) {
            // Image or placeholder
            DSAsyncImage(
                imageUrl = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            
            // Type badge capsule (black background like iOS)
            DSTypeBadgeCapsule(
                type = type,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
        }
        
        // Info section - FIXED HEIGHT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoHeight)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Title - 20sp semibold, 1 line
            Text(
                text = title,
                style = RixyTypography.H4.copy(
                    fontSize = 20.sp // Slightly larger for hero
                ),
                color = RixyColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Business name - always show space if null
            Text(
                text = businessName ?: " ",
                style = RixyTypography.Subtext,
                color = RixyColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Price with semibold
            Text(
                text = priceFormatted ?: " ",
                style = RixyTypography.Subtext.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = RixyColors.Brand,
                maxLines = 1
            )
        }
    }
}

/**
 * DSCategoryCard - Grid card for category selection
 * 
 * Replicates CategoryCard from CityHomeView.swift:
 * - 2-column grid
 * - 120dp min height
 * - Emoji icon 40sp
 * - Background color with 0.1 alpha
 * - 16dp border radius
 */
@Composable
fun DSCategoryCard(
    title: String,
    emoji: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = emoji,
            style = RixyTypography.H2.copy(fontSize = 40.sp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = RixyTypography.Body,
            color = RixyColors.TextPrimary
        )
    }
}

/**
 * CityHeroSection - Hero banner for city home
 * 
 * Replicates CityHeroSection from CityHomeView.swift:
 * - 200dp height
 * - 24dp border radius
 * - Gradient background (Brand → Brand Light)
 * - City name, location, stats
 */
@Composable
fun DSCityHeroSection(
    cityName: String,
    location: String?,
    businessCount: Int?,
    listingCount: Int?,
    subscriptionCount: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        RixyColors.Brand,
                        RixyColors.Brand.copy(red = 1f, green = 0.4f, blue = 0.5f) // Brand light
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // City name
            Text(
                text = cityName,
                style = RixyTypography.H1,
                color = Color.White
            )
            
            // Location
            location?.let {
                Text(
                    text = it,
                    style = RixyTypography.Body,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                StatItem(
                    value = formatCount(businessCount),
                    label = "Negocios",
                    textColor = Color.White
                )
                StatItem(
                    value = formatCount(listingCount),
                    label = "Publicaciones",
                    textColor = Color.White
                )
                StatItem(
                    value = formatCount(subscriptionCount),
                    label = "Suscripciones",
                    textColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = RixyTypography.H3.copy(
                fontSize = 22.sp,
                color = textColor
            )
        )
        Text(
            text = label,
            style = RixyTypography.Caption,
            color = textColor.copy(alpha = 0.8f)
        )
    }
}

private fun formatCount(count: Int?): String {
    return count?.toString() ?: "N/D"
}

package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors

/**
 * DSSkeleton - iOS-style shimmer loading placeholder
 * 
 * Creates a shimmer effect similar to iOS skeleton views:
 * - Animated gradient that sweeps across the placeholder
 * - Subtle, elegant animation (not too fast)
 * - Matches the shape of the actual content
 */
@Composable
fun DSSkeleton(
    modifier: Modifier = Modifier,
    color: Color = RixyColors.Border.copy(alpha = 0.3f),
    shimmerColor: Color = RixyColors.Border.copy(alpha = 0.5f),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    val shimmerColors = listOf(
        color,
        shimmerColor,
        color
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 500f, 0f),
        end = Offset(translateAnim.value, 0f)
    )
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * Skeleton for listing card (horizontal)
 * Matches DSListingCard dimensions
 */
@Composable
fun DSListingCardSkeleton(
    modifier: Modifier = Modifier
) {
    val cardWidth = 260.dp
    val imageHeight = 195.dp
    val infoHeight = 100.dp
    
    Column(
        modifier = modifier.width(cardWidth)
    ) {
        // Image skeleton
        DSSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )
        
        // Info skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(infoHeight)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Title lines
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Business
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Skeleton for compact listing card (vertical list)
 */
@Composable
fun DSListingCardCompactSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Thumbnail
        DSSkeleton(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Type badge
            DSSkeleton(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price
            DSSkeleton(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Skeleton for hero card
 */
@Composable
fun DSHeroCardSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Image
        DSSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )
        
        // Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            DSSkeleton(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
            )
        }
    }
}

/**
 * Skeleton for category card
 */
@Composable
fun DSCategoryCardSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        DSSkeleton(modifier = Modifier.fillMaxWidth())
    }
}

/**
 * Text skeleton - single line
 */
@Composable
fun DSTextSkeleton(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f
) {
    DSSkeleton(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(16.dp)
    )
}

/**
 * Circle skeleton - for avatars, profile pictures
 */
@Composable
fun DSCircleSkeleton(
    size: androidx.compose.ui.unit.Dp = 48.dp) {
    DSSkeleton(
        modifier = Modifier.size(size),
        shape = androidx.compose.foundation.shape.CircleShape
    )
}

/**
 * City hero skeleton
 */
@Composable
fun DSCityHeroSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        DSSkeleton(modifier = Modifier.fillMaxWidth())
    }
}

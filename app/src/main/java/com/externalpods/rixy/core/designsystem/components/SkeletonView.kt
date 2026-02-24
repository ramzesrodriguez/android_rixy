package com.externalpods.rixy.core.designsystem.components

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
 * Skeleton loading placeholder with shimmer effect
 */
@Composable
private fun ShimmerBrush(): Brush {
    val shimmerColors = listOf(
        RixyColors.SurfaceVariant.copy(alpha = 0.6f),
        RixyColors.SurfaceVariant.copy(alpha = 0.2f),
        RixyColors.SurfaceVariant.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

/**
 * Skeleton box with shimmer effect
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ShimmerBrush())
    )
}

/**
 * Skeleton for listing card
 */
@Composable
fun ListingCardSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Image skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(12.dp))
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Title skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Price skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(14.dp)
        )
    }
}

/**
 * Skeleton for city card
 */
@Composable
fun CityCardSkeleton(
    modifier: Modifier = Modifier
) {
    SkeletonBox(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .clip(RoundedCornerShape(16.dp))
    )
}

/**
 * Skeleton for horizontal listing item
 */
@Composable
fun ListingHorizontalSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Thumbnail skeleton
        SkeletonBox(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Type badge skeleton
            SkeletonBox(
                modifier = Modifier
                    .width(60.dp)
                    .height(14.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title skeleton
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Second line skeleton
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
            )
        }
    }
}

/**
 * Skeleton for text lines
 */
@Composable
fun TextSkeleton(
    lines: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        repeat(lines) { index ->
            val width = if (index == lines - 1) 0.6f else 1f
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(width)
                    .height(14.dp)
            )
            if (index < lines - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Skeleton for circular avatar
 */
@Composable
fun AvatarSkeleton(
    size: Int = 48,
    modifier: Modifier = Modifier
) {
    SkeletonBox(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(50))
    )
}

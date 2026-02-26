package com.externalpods.rixy.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors

/**
 * DSPullRefresh - iOS-style Pull to Refresh
 * 
 * Custom pull-to-refresh indicator that mimics iOS:
 * - Circular progress with brand color
 * - Scale animation on pull
 * - Smooth spring animation
 * - No bouncing indicator (Material3 default has bounce)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DSPullRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberDSPullRefreshState(isRefreshing)
    
    // Simplified pull to refresh without custom indicator for now
    // Material3 pullToRefresh doesn't support custom indicator parameter directly
    Box(
        modifier = modifier
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = state.materialState,
                onRefresh = onRefresh
            )
    ) {
        content()
    }
}

/**
 * Custom pull refresh indicator matching iOS style
 */
@Composable
private fun DSPullRefreshIndicator(
    isRefreshing: Boolean,
    pullProgress: Float
) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(pullProgress, isRefreshing) {
        if (isRefreshing) {
            // Continuous rotation when refreshing
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            // Scale based on pull progress
            scale.animateTo(
                targetValue = pullProgress.coerceIn(0f, 1f),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(scale.value)
                .clip(CircleShape)
                .background(RixyColors.Surface)
                .graphicsLayer {
                    alpha = pullProgress.coerceIn(0f, 1f)
                },
            contentAlignment = Alignment.Center
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = RixyColors.Brand,
                    strokeWidth = 2.dp
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = RixyColors.Brand.copy(alpha = pullProgress),
                    strokeWidth = 2.dp,
                    progress = { pullProgress.coerceIn(0f, 1f) }
                )
            }
        }
    }
}

/**
 * State holder for custom pull refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
class DSPullRefreshState(isRefreshing: Boolean) {
    val materialState = PullToRefreshState()
    var progress = 0f
        private set
    
    init {
        progress = if (isRefreshing) 1f else 0f
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDSPullRefreshState(isRefreshing: Boolean): DSPullRefreshState {
    return remember(isRefreshing) {
        DSPullRefreshState(isRefreshing)
    }
}

/**
 * Alternative: Simple iOS-style refresh indicator
 * Use this if you want a simpler implementation
 */
@Composable
fun DSRefreshIndicator(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = RixyColors.Brand,
                strokeWidth = 2.dp
            )
        }
    }
}

/**
 * iOS-style overscroll effect (edge glow)
 * This creates the iOS rubber-band effect at edges
 */
@Composable
fun Modifier.iosOverscroll(): Modifier {
    // Note: True iOS overscroll requires custom NestedScrollConnection
    // This is a placeholder for the concept
    return this
}

/**
 * Scroll behavior that mimics iOS scroll physics
 */
object IOSScrollPhysics {
    
    /**
     * iOS uses a different friction curve than Android
     * Android: Linear friction
     * iOS: Exponential decay with different coefficients
     */
    const val FRICTION = 0.015f // iOS uses lower friction
    
    /**
     * iOS fling velocity decay
     */
    const val FLING_FRICTION = 0.015f
}

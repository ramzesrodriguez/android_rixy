package com.externalpods.rixy.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import kotlin.math.max
import kotlin.math.min

/**
 * CollapsingToolbarLayout - Reusable parallax collapsing toolbar
 * 
 * Features:
 * - Header image stays fixed (parallax effect)
 * - Content scrolls over the header
 * - Toolbar transitions from transparent to solid
 * - Rounded sheet slides up over header
 */
@Composable
fun CollapsingToolbarLayout(
    headerHeight: Dp = 400.dp,
    toolbarHeight: Dp = 56.dp,
    title: String,
    scrollState: LazyListState = rememberLazyListState(),
    isFavorite: Boolean = false,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    headerContent: @Composable () -> Unit,
    content: LazyListScope.() -> Unit
) {
    val density = LocalDensity.current
    val headerHeightPx = with(density) { headerHeight.toPx() }
    val toolbarHeightPx = with(density) { toolbarHeight.toPx() }
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    
    // Calculate scroll progress based on how much we've scrolled
    val scrollProgress by remember {
        derivedStateOf {
            val firstItem = scrollState.firstVisibleItemIndex
            val offset = scrollState.firstVisibleItemScrollOffset.toFloat()
            
            if (firstItem == 0) {
                // First item is the spacer - calculate progress based on offset
                val collapseThreshold = headerHeightPx - statusBarHeight - toolbarHeightPx
                min(1f, max(0f, offset / collapseThreshold))
            } else {
                // Scrolled past header
                1f
            }
        }
    }
    
    // Title fades in when collapsed
    val animatedTitleAlpha by animateFloatAsState(
        targetValue = if (scrollProgress > 0.7f) (scrollProgress - 0.7f) / 0.3f else 0f,
        label = "title_alpha"
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Fixed header - stays in place (creates parallax effect)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
        ) {
            headerContent()
        }
        
        // Scrollable content that slides over the header
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            // Spacer pushes content to start at bottom of header
            item {
                Spacer(modifier = Modifier.height(headerHeight - toolbarHeight))
            }
            
            // Rounded sheet that slides over header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = RixyColors.Background,
                    shadowElevation = 4.dp,
                    tonalElevation = 0.dp
                ) {
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }
            
            // User content
            content()
            
            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        
        // Collapsing toolbar overlay
        CollapsingToolbar(
            title = title,
            scrollProgress = scrollProgress,
            titleAlpha = animatedTitleAlpha,
            isFavorite = isFavorite,
            onBackClick = onBackClick,
            onFavoriteToggle = onFavoriteToggle
        )
    }
}

/**
 * Collapsing toolbar with animated background
 */
@Composable
private fun CollapsingToolbar(
    title: String,
    scrollProgress: Float,
    titleAlpha: Float,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (scrollProgress > 0.8f) RixyColors.Surface else Color.Transparent,
        label = "toolbar_bg"
    )
    
    val iconBackgroundColor by animateColorAsState(
        targetValue = if (scrollProgress > 0.8f) Color.Transparent else Color.White.copy(alpha = 0.9f),
        label = "icon_bg"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .background(backgroundColor)
    ) {
        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (scrollProgress > 0.8f) RixyColors.Brand else RixyColors.TextPrimary
            )
        }
        
        // Title (fades in when collapsed)
        if (titleAlpha > 0.01f) {
            Text(
                text = title,
                style = RixyTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = RixyColors.TextPrimary,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer { alpha = titleAlpha }
            )
        }
        
        // Favorite button
        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) RixyColors.Error else RixyColors.TextPrimary
            )
        }
    }
}

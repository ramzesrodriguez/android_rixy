package com.externalpods.rixy.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSNavigationBar - iOS-style Bottom Tab Bar
 * 
 * Replicates iOS TabView with:
 * - Icon scale animation on selection (1.1x)
 * - No indicator (Material3 default indicator removed)
 * - Brand color for selected items
 * - Haptic feedback on tab change
 * - Labels always visible (like iOS)
 */
@Composable
fun DSNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<DSTabItem>,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = RixyColors.Surface,
        tonalElevation = 0.dp // No elevation shadow
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            
            // iOS-style scale animation
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                animationSpec = spring(
                    stiffness = 400f,
                    dampingRatio = 0.8f
                ),
                label = "tab_icon_scale"
            )
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(iconScale)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = RixyTypography.CaptionSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    if (index != selectedTab) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onTabSelected(index)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RixyColors.Brand,
                    selectedTextColor = RixyColors.Brand,
                    unselectedIconColor = RixyColors.TextSecondary,
                    unselectedTextColor = RixyColors.TextSecondary,
                    indicatorColor = Color.Transparent // No indicator
                ),
                alwaysShowLabel = true // iOS always shows labels
            )
        }
    }
}

/**
 * Tab item data class
 */
data class DSTabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Standard user tabs for Rixy
 */
object DSUserTabs {
    val Home = DSTabItem(
        label = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    val Search = DSTabItem(
        label = "Buscar",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )
    
    val Favorites = DSTabItem(
        label = "Favoritos",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )
    
    val Orders = DSTabItem(
        label = "Pedidos",
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag
    )
    
    val Profile = DSTabItem(
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    
    val All = listOf(Home, Search, Favorites, Orders, Profile)
}

/**
 * Complete User Tab Bar screen scaffold
 * Use this as replacement for UserTabBar
 */
@Composable
fun DSUserTabBarScaffold(
    homeContent: @Composable () -> Unit,
    searchContent: @Composable () -> Unit,
    favoritesContent: @Composable () -> Unit,
    ordersContent: @Composable () -> Unit,
    profileContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    
    Scaffold(
        modifier = modifier,
        bottomBar = {
            DSNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                tabs = DSUserTabs.All
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> homeContent()
                1 -> searchContent()
                2 -> favoritesContent()
                3 -> ordersContent()
                4 -> profileContent()
            }
        }
    }
}

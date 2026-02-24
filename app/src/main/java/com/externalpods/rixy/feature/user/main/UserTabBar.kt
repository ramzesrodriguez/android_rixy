package com.externalpods.rixy.feature.user.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.feature.settings.SettingsScreen
import com.externalpods.rixy.feature.user.browse.BrowseListingsScreen
import com.externalpods.rixy.feature.user.cityhome.CityHomeScreen
import com.externalpods.rixy.feature.user.favorites.FavoritesScreen

enum class UserTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    SEARCH("Buscar", Icons.Filled.Search, Icons.Outlined.Search),
    FAVORITES("Favoritos", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    ORDERS("Pedidos", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
    PROFILE("Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun UserTabBar(
    city: City,
    onListingClick: (Listing) -> Unit,
    onNavigateToLogin: () -> Unit,
    onModeChanged: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                UserTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeTab(
                city = city,
                onListingClick = onListingClick,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> SearchTab(
                cityId = city.id,
                onListingClick = onListingClick,
                modifier = Modifier.padding(paddingValues)
            )
            2 -> FavoritesTab(
                onListingClick = onListingClick,
                modifier = Modifier.padding(paddingValues)
            )
            3 -> OrdersTab(modifier = Modifier.padding(paddingValues))
            4 -> ProfileTab(
                onNavigateToLogin = onNavigateToLogin,
                onModeChanged = onModeChanged,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun HomeTab(
    city: City,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "city_home",
        modifier = modifier
    ) {
        composable("city_home") {
            CityHomeScreen(
                city = city,
                onListingClick = onListingClick,
                onSeeAllListings = {}
            )
        }
    }
}

@Composable
private fun SearchTab(
    cityId: String,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    BrowseListingsScreen(
        cityId = cityId,
        onBackClick = {},
        onListingClick = onListingClick,
        modifier = modifier
    )
}

@Composable
private fun FavoritesTab(
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    FavoritesScreen(
        onListingClick = onListingClick,
        onBackClick = {},
        modifier = modifier
    )
}

@Composable
private fun OrdersTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = RixyColors.TextTertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pedidos", style = RixyTypography.H3, color = RixyColors.TextPrimary)
            Text("Próximamente", style = RixyTypography.Body, color = RixyColors.TextSecondary)
        }
    }
}

@Composable
private fun ProfileTab(
    onNavigateToLogin: () -> Unit,
    onModeChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsScreen(
        onNavigateToLogin = onNavigateToLogin,
        onModeChanged = { onModeChanged() },
        modifier = modifier
    )
}

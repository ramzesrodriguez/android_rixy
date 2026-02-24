package com.externalpods.rixy.feature.user.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.externalpods.rixy.R
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.externalpods.rixy.core.designsystem.components.RixyButton
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.feature.settings.SettingsScreen
import com.externalpods.rixy.feature.user.browse.BrowseListingsScreen
import com.externalpods.rixy.feature.user.cityhome.CityHomeScreen
import com.externalpods.rixy.feature.user.favorites.FavoritesScreen
import com.externalpods.rixy.feature.user.orders.OrdersScreen

enum class UserTab(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    SEARCH(R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search),
    FAVORITES(R.string.nav_favorites, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    ORDERS(R.string.nav_orders, Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
    PROFILE(R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun UserTabBar(
    city: City,
    isAuthenticated: Boolean,
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
                                contentDescription = stringResource(tab.labelRes)
                            )
                        },
                        label = { Text(stringResource(tab.labelRes)) },
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
                isAuthenticated = isAuthenticated,
                onListingClick = onListingClick,
                onNavigateToLogin = onNavigateToLogin,
                modifier = Modifier.padding(paddingValues)
            )
            3 -> OrdersTab(
                isAuthenticated = isAuthenticated,
                onNavigateToLogin = onNavigateToLogin,
                modifier = Modifier.padding(paddingValues)
            )
            4 -> ProfileTab(
                isAuthenticated = isAuthenticated,
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
    isAuthenticated: Boolean,
    onListingClick: (Listing) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isAuthenticated) {
        FavoritesScreen(
            onListingClick = onListingClick,
            onBackClick = {},
            modifier = modifier
        )
    } else {
        LoginRequiredScreen(
            title = stringResource(R.string.nav_favorites),
            message = stringResource(R.string.empty_no_favorites_login),
            onLoginClick = onNavigateToLogin,
            modifier = modifier
        )
    }
}

@Composable
private fun OrdersTab(
    isAuthenticated: Boolean,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isAuthenticated) {
        OrdersScreen(
            onBackClick = {},
            modifier = modifier
        )
    } else {
        LoginRequiredScreen(
            title = stringResource(R.string.nav_orders),
            message = stringResource(R.string.empty_no_orders_login),
            onLoginClick = onNavigateToLogin,
            modifier = modifier
        )
    }
}

@Composable
private fun ProfileTab(
    isAuthenticated: Boolean,
    onNavigateToLogin: () -> Unit,
    onModeChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isAuthenticated) {
        SettingsScreen(
            onNavigateToLogin = onNavigateToLogin,
            onModeChanged = { onModeChanged() },
            modifier = modifier
        )
    } else {
        LoginRequiredScreen(
            title = stringResource(R.string.nav_profile),
            message = stringResource(R.string.login_required_message),
            onLoginClick = onNavigateToLogin,
            modifier = modifier
        )
    }
}

@Composable
private fun LoginRequiredScreen(
    title: String,
    message: String,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = RixyColors.TextTertiary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = RixyTypography.H3,
                color = RixyColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))
            RixyButton(
                text = stringResource(R.string.auth_login),
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

package com.externalpods.rixy.feature.user.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.externalpods.rixy.core.designsystem.components.EmptyStateNoCity
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.feature.auth.LoginScreen
import com.externalpods.rixy.feature.auth.RegisterScreen
import com.externalpods.rixy.feature.settings.SettingsViewModel
import com.externalpods.rixy.feature.settings.CityPickerSheet
import com.externalpods.rixy.feature.settings.LanguagePickerSheet
import com.externalpods.rixy.feature.user.cityhome.CityHomeScreen
import com.externalpods.rixy.feature.user.cityselector.CitySelectorScreen
import com.externalpods.rixy.feature.user.cityselector.CitySelectorViewModel
import com.externalpods.rixy.feature.user.favorites.FavoritesScreen
import com.externalpods.rixy.feature.user.orders.OrdersScreen
import com.externalpods.rixy.feature.settings.SettingsScreen
import com.externalpods.rixy.feature.settings.ModePickerSheet
import com.externalpods.rixy.feature.user.browse.BrowseListingsScreen
import com.externalpods.rixy.feature.user.businessprofile.BusinessProfileScreen
import com.externalpods.rixy.feature.user.listingdetail.ListingDetailScreen
import com.externalpods.rixy.navigation.AppStateViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.WindowInsets

/**
 * UserTabBarView - iOS-style Tab Bar (mirrors iOS UserTabBarView)
 * 
 * Features:
 * - 5 tabs: Home, Search, Favorites, Orders, Profile
 * - Each tab has its OWN NavigationHost (like iOS NavigationStack)
 * - Icon scale animation on selection (1.1x)
 * - Brand tint color
 */
@Composable
fun UserTabBarView(
    appState: AppStateViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current
    
    val tabs = listOf(
        TabItem("Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        TabItem("Buscar", Icons.Filled.Search, Icons.Outlined.Search),
        TabItem("Favoritos", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        TabItem("Pedidos", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
        TabItem("Perfil", Icons.Filled.Person, Icons.Outlined.Person)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = RixyColors.Surface,
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    
                    NavigationBarItem(
                        icon = {
                            AnimatedContent(
                                targetState = isSelected,
                                transitionSpec = {
                                    scaleIn(
                                        initialScale = 0.8f,
                                        animationSpec = tween(200)
                                    ) togetherWith fadeOut(animationSpec = tween(100))
                                },
                                label = "tab_icon_$index"
                            ) { selected ->
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.scale(if (selected) 1.1f else 1f)
                                )
                            }
                        },
                        label = { Text(tab.label, style = RixyTypography.CaptionSmall) },
                        selected = isSelected,
                        onClick = {
                            if (index != selectedTab) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedTab = index
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RixyColors.Brand,
                            selectedTextColor = RixyColors.Brand,
                            unselectedIconColor = RixyColors.TextSecondary,
                            unselectedTextColor = RixyColors.TextSecondary,
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> HomeTab(appState = appState)
                1 -> SearchTab(appState = appState)
                2 -> FavoritesTab(appState = appState)
                3 -> OrdersTab()
                4 -> ProfileTab(appState = appState)
            }
        }
    }
}

data class TabItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

// MARK: - Home Tab (mirrors iOS HomeTabView)
@Composable
fun HomeTab(
    appState: AppStateViewModel
) {
    val navController = rememberNavController()
    val selectedCity by appState.selectedCity.collectAsStateWithLifecycle()
    
    NavHost(
        navController = navController,
        startDestination = if (selectedCity != null) "city_home" else "city_selector"
    ) {
        composable("city_home") {
            selectedCity?.let { city ->
                CityHomeScreen(
                    city = city,
                    onListingClick = { listing ->
                        navController.navigate(ListingDetailRoute(listing.id, city.slug))
                    },
                    onSeeAllListings = {
                        navController.navigate(BrowseRoute(city.slug))
                    },
                    onChangeCity = {
                        navController.navigate("city_selector")
                    },
                    onBusinessCTAClick = {
                        // Handle business CTA
                    }
                )
            }
        }
        
        composable("city_selector") {
            CitySelectorScreen(
                onCitySelected = { city ->
                    appState.selectCity(city)
                    navController.navigate("city_home") {
                        popUpTo("city_selector") { inclusive = true }
                    }
                },
                onBackClick = null
            )
        }
        
        // Type-safe navigation destinations
        composable<ListingDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ListingDetailRoute>()
            ListingDetailScreen(
                listingId = route.listingId,
                citySlug = route.citySlug,
                onBackClick = { navController.popBackStack() },
                onBusinessClick = { businessId ->
                    navController.navigate(BusinessProfileRoute(businessId, route.citySlug))
                },
                onShareClick = { }
            )
        }
        
        composable<BusinessProfileRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BusinessProfileRoute>()
            BusinessProfileScreen(
                citySlug = route.citySlug,
                businessId = route.businessId,
                onBackClick = { navController.popBackStack() },
                onListingClick = { listing ->
                    navController.navigate(ListingDetailRoute(listing.id, route.citySlug))
                },
                onPhoneClick = { }
            )
        }
        
        composable<BrowseRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BrowseRoute>()
            BrowseListingsScreen(
                citySlug = route.citySlug,
                onBackClick = { navController.popBackStack() },
                onListingClick = { listing ->
                    navController.navigate(ListingDetailRoute(listing.id, route.citySlug))
                }
            )
        }
    }
}

// MARK: - Search Tab
@Composable
fun SearchTab(
    appState: AppStateViewModel
) {
    val navController = rememberNavController()
    val selectedCity by appState.selectedCity.collectAsStateWithLifecycle()
    
    NavHost(
        navController = navController,
        startDestination = "search_main"
    ) {
        composable("search_main") {
            if (selectedCity != null) {
                BrowseListingsScreen(
                    citySlug = selectedCity!!.slug,
                    onBackClick = null, // No back in search tab
                    onListingClick = { listing ->
                        navController.navigate(ListingDetailRoute(listing.id, selectedCity!!.slug))
                    }
                )
            } else {
                SelectCityPromptScreen(
                    onSelectCity = {
                        navController.navigate("city_selector_sheet")
                    }
                )
            }
        }
        
        composable("city_selector_sheet") {
            CitySelectorScreen(
                onCitySelected = { city ->
                    appState.selectCity(city)
                    navController.navigate("search_main") {
                        popUpTo("search_main") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable<ListingDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ListingDetailRoute>()
            ListingDetailScreen(
                listingId = route.listingId,
                citySlug = route.citySlug,
                onBackClick = { navController.popBackStack() },
                onBusinessClick = { businessId ->
                    navController.navigate(BusinessProfileRoute(businessId, route.citySlug))
                },
                onShareClick = { }
            )
        }
        
        composable<BusinessProfileRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BusinessProfileRoute>()
            BusinessProfileScreen(
                citySlug = route.citySlug,
                businessId = route.businessId,
                onBackClick = { navController.popBackStack() },
                onListingClick = { listing ->
                    navController.navigate(ListingDetailRoute(listing.id, route.citySlug))
                },
                onPhoneClick = { }
            )
        }
    }
}

// MARK: - Favorites Tab
@Composable
fun FavoritesTab(
    appState: AppStateViewModel
) {
    val navController = rememberNavController()
    val selectedCity by appState.selectedCity.collectAsStateWithLifecycle()
    
    NavHost(
        navController = navController,
        startDestination = "favorites_main"
    ) {
        composable("favorites_main") {
            FavoritesScreen(
                onListingClick = { listing ->
                    val citySlug = selectedCity?.slug
                    if (citySlug != null) {
                        navController.navigate(ListingDetailRoute(listing.id, citySlug))
                    }
                },
                onBackClick = null // No back in favorites tab
            )
        }
        
        composable<ListingDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ListingDetailRoute>()
            ListingDetailScreen(
                listingId = route.listingId,
                citySlug = route.citySlug,
                onBackClick = { navController.popBackStack() },
                onBusinessClick = { },
                onShareClick = { }
            )
        }
    }
}

// MARK: - Orders Tab
@Composable
fun OrdersTab() {
    OrdersScreen(onBackClick = null)
}

// MARK: - Profile Tab
@Composable
fun ProfileTab(
    appState: AppStateViewModel
) {
    val navController = rememberNavController()
    val isAuthenticated by appState.isAuthenticated
    val selectedCity by appState.selectedCity.collectAsStateWithLifecycle()
    val currentUser by appState.currentUser.collectAsStateWithLifecycle()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val citySelectorViewModel: CitySelectorViewModel = koinViewModel()
    val citySelectorUiState by citySelectorViewModel.uiState.collectAsStateWithLifecycle()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }
    val canUseOwnerMode = isAuthenticated && (
        currentUser?.role == com.externalpods.rixy.core.model.OwnerRole.OWNER ||
            currentUser?.role == com.externalpods.rixy.core.model.OwnerRole.ADMIN
        )
    
    NavHost(
        navController = navController,
        startDestination = "profile_main"
    ) {
        composable("profile_main") {
            SettingsScreen(
                isAuthenticated = isAuthenticated,
                selectedCityName = selectedCity?.name,
                userEmail = settingsUiState.userEmail.takeIf { it.isNotBlank() },
                languageLabel = settingsUiState.languageLabel,
                currentModeLabel = when (settingsUiState.currentMode) {
                    com.externalpods.rixy.core.model.AppMode.USER -> "Usuario"
                    com.externalpods.rixy.core.model.AppMode.OWNER -> "Negocio"
                    com.externalpods.rixy.core.model.AppMode.ADMIN -> "Administrador"
                },
                canUseOwnerMode = canUseOwnerMode,
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onModeChanged = { showModeDialog = true },
                onSignOut = settingsViewModel::signOut,
                onBackClick = null,
                onLanguageClick = { showLanguageDialog = true },
                onChangeCityClick = { showCityDialog = true }
            )
        }
        
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.popBackStack("profile_main", inclusive = false)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack("login", inclusive = false)
                }
            )
        }
    }

    if (showLanguageDialog) {
        LanguagePickerSheet(
            selectedLanguageTag = settingsUiState.languageTag,
            onSelectLanguage = { tag ->
                settingsViewModel.setLanguage(tag)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showCityDialog) {
        CityPickerSheet(
            selectedCityId = selectedCity?.id,
            cities = citySelectorUiState.filteredCities,
            searchQuery = citySelectorUiState.searchQuery,
            isLoading = citySelectorUiState.isLoading,
            error = citySelectorUiState.error,
            onSearchQueryChange = citySelectorViewModel::onSearchQueryChange,
            onSelectCity = { city ->
                appState.selectCity(city)
                showCityDialog = false
            },
            onRetry = citySelectorViewModel::refresh,
            onDismiss = { showCityDialog = false }
        )
    }

    if (showModeDialog) {
        val availableModes = buildList {
            add(com.externalpods.rixy.core.model.AppMode.USER)
            if (canUseOwnerMode) add(com.externalpods.rixy.core.model.AppMode.OWNER)
            if (currentUser?.role == com.externalpods.rixy.core.model.OwnerRole.ADMIN) {
                add(com.externalpods.rixy.core.model.AppMode.ADMIN)
            }
        }
        ModePickerSheet(
            selectedMode = settingsUiState.currentMode,
            availableModes = availableModes,
            onSelectMode = { mode ->
                settingsViewModel.switchMode(mode)
                showModeDialog = false
            },
            onDismiss = { showModeDialog = false }
        )
    }
}

// MARK: - Select City Prompt (mirrors iOS SelectCityPromptView)
@Composable
fun SelectCityPromptScreen(
    onSelectCity: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        EmptyStateNoCity(
            onSelectCity = onSelectCity
        )
    }
}

// Type-safe navigation routes (mirrors iOS NavigationDestination)
@Serializable
data class ListingDetailRoute(val listingId: String, val citySlug: String)

@Serializable
data class BusinessProfileRoute(val businessId: String, val citySlug: String)

@Serializable
data class BrowseRoute(val citySlug: String)

@Serializable
data class CategoryRoute(val citySlug: String, val type: String)

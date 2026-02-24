package com.externalpods.rixy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.externalpods.rixy.core.model.AppMode
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.feature.auth.LoginScreen
import com.externalpods.rixy.feature.auth.RegisterScreen
import com.externalpods.rixy.feature.owner.dashboard.OwnerDashboardScreen
import com.externalpods.rixy.feature.settings.SettingsScreen
import com.externalpods.rixy.feature.user.browse.BrowseListingsScreen
import com.externalpods.rixy.feature.user.cityhome.CityHomeScreen
import com.externalpods.rixy.feature.user.cityselector.CitySelectorScreen
import com.externalpods.rixy.feature.user.listingdetail.ListingDetailScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Main navigation graph for Rixy app
 */
@Composable
fun RixyNavGraph(
    appState: AppState = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val currentMode by appState.currentMode.collectAsStateWithLifecycle()
    val isAuthenticated by appState.isAuthenticated.collectAsStateWithLifecycle()
    val selectedCity by appState.selectedCity.collectAsStateWithLifecycle()
    
    // Determine start destination based on state
    val startDestination = when {
        !isAuthenticated -> Screen.Login
        selectedCity == null -> Screen.CitySelector
        else -> when (currentMode) {
            AppMode.USER -> Screen.CityHome(selectedCity!!.id, selectedCity!!.slug)
            AppMode.OWNER -> Screen.OwnerDashboard
            AppMode.ADMIN -> Screen.AdminDashboard
            else -> Screen.CitySelector
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Routes
        composable<Screen.Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.CitySelector) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.Register> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.CitySelector) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }
        
        // User Mode Routes
        composable<Screen.CitySelector> {
            CitySelectorScreen(
                onCitySelected = { city ->
                    appState.selectCity(city)
                    navController.navigate(Screen.CityHome(city.id, city.slug)) {
                        popUpTo(Screen.CitySelector) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.CityHome> { backStackEntry ->
            val city = selectedCity ?: City(
                id = "",
                name = "Ciudad",
                slug = ""
            )
            
            CityHomeScreen(
                city = city,
                onListingClick = { listing ->
                    navController.navigate(Screen.ListingDetail(listing.id))
                },
                onSeeAllListings = {
                    navController.navigate(Screen.Browse(cityId = city.id))
                }
            )
        }
        
        composable<Screen.ListingDetail> { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            ListingDetailScreen(
                listingId = listingId,
                onBackClick = {
                    navController.popBackStack()
                },
                onBusinessClick = { businessId ->
                    navController.navigate(Screen.BusinessProfile(businessId))
                }
            )
        }
        
        composable<Screen.BusinessProfile> { backStackEntry ->
            // TODO: Implement BusinessProfileScreen
            // For now, just go back
            navController.popBackStack()
        }
        
        composable<Screen.Browse> { backStackEntry ->
            val cityId = backStackEntry.arguments?.getString("cityId")
            BrowseListingsScreen(
                cityId = cityId,
                onBackClick = {
                    navController.popBackStack()
                },
                onListingClick = { listing ->
                    navController.navigate(Screen.ListingDetail(listing.id))
                }
            )
        }
        
        // Owner Mode Routes
        composable<Screen.OwnerDashboard> {
            OwnerDashboardScreen(
                onNavigateToBusiness = {
                    // TODO: Navigate to business editor
                },
                onNavigateToListings = {
                    // TODO: Navigate to listings
                },
                onNavigateToFeatured = {
                    // TODO: Navigate to featured campaigns
                },
                onNavigateToCitySlots = {
                    // TODO: Navigate to city slots
                },
                onNavigateToCreateListing = {
                    // TODO: Navigate to create listing
                }
            )
        }
        
        // Admin Mode Routes
        composable<Screen.AdminDashboard> {
            // TODO: Implement AdminDashboardScreen
            // For now, show a placeholder
            androidx.compose.material3.Text("Admin Dashboard - Not implemented yet")
        }
        
        // Common Routes
        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onModeChanged = { mode ->
                    when (mode) {
                        AppMode.USER -> {
                            selectedCity?.let { city ->
                                navController.navigate(Screen.CityHome(city.id, city.slug)) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        AppMode.OWNER -> {
                            navController.navigate(Screen.OwnerDashboard) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        AppMode.ADMIN -> {
                            navController.navigate(Screen.AdminDashboard) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                }
            )
        }
    }
}

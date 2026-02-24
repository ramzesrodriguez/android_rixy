package com.externalpods.rixy.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.externalpods.rixy.core.designsystem.navigation.RixyNavigationTransitions
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.feature.user.cityhome.CityHomeScreenV2

/**
 * Navigation routes sealed class (type-safe)
 */
sealed class ScreenV2(val route: String) {
    data object CityHome : ScreenV2("city_home")
    data object Browse : ScreenV2("browse")
    data object ListingDetail : ScreenV2("listing_detail/{listingId}") {
        fun createRoute(listingId: String) = "listing_detail/$listingId"
    }
    data object BusinessProfile : ScreenV2("business/{businessId}") {
        fun createRoute(businessId: String) = "business/$businessId"
    }
    data object Favorites : ScreenV2("favorites")
    data object Orders : ScreenV2("orders")
    data object Profile : ScreenV2("profile")
    data object Settings : ScreenV2("settings")
    data object Login : ScreenV2("login")
    data object Register : ScreenV2("register")
}

/**
 * RixyNavHostV2 - Navigation with iOS-style transitions
 * 
 * Features:
 * - Push/Pop transitions (slide from right)
 * - Modal transitions (slide from bottom)
 * - Fade transitions for tabs
 * - No transition for same-level navigation
 */
@Composable
fun RixyNavHostV2(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ScreenV2.CityHome.route,
    city: City,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize(),
        enterTransition = { RixyNavigationTransitions.pushEnter(this) },
        exitTransition = { RixyNavigationTransitions.pushExit(this) },
        popEnterTransition = { RixyNavigationTransitions.popEnter(this) },
        popExitTransition = { RixyNavigationTransitions.popExit(this) }
    ) {
        // City Home (main screen)
        composable(
            route = ScreenV2.CityHome.route,
            enterTransition = { EnterTransition.None }, // No animation for start destination
            exitTransition = { ExitTransition.None }
        ) {
            CityHomeScreenV2(
                city = city,
                onListingClick = { listing ->
                    navController.navigate(ScreenV2.ListingDetail.createRoute(listing.id))
                },
                onSeeAllListings = {
                    navController.navigate(ScreenV2.Browse.route)
                },
                onChangeCity = {
                    // Navigate to city selector
                    // navController.navigate(ScreenV2.CitySelector.route)
                },
                onBusinessCTAClick = {
                    navController.navigate(ScreenV2.Login.route)
                }
            )
        }
        
        // Browse / Search
        composable(
            route = ScreenV2.Browse.route
        ) {
            // BrowseListingsScreenV2(
            //     cityId = city.id,
            //     onBackClick = { navController.popBackStack() },
            //     onListingClick = { listing ->
            //         navController.navigate(ScreenV2.ListingDetail.createRoute(listing.id))
            //     }
            // )
            PlaceholderScreen("Buscar", onBack = { navController.popBackStack() })
        }
        
        // Listing Detail
        composable(
            route = ScreenV2.ListingDetail.route
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")
            // ListingDetailScreenV2(
            //     listingId = listingId,
            //     onBackClick = { navController.popBackStack() },
            //     onBusinessClick = { businessId ->
            //         navController.navigate(ScreenV2.BusinessProfile.createRoute(businessId))
            //     }
            // )
            PlaceholderScreen("Detalle: $listingId", onBack = { navController.popBackStack() })
        }
        
        // Business Profile
        composable(
            route = ScreenV2.BusinessProfile.route
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId")
            // BusinessProfileScreenV2(
            //     businessId = businessId,
            //     onBackClick = { navController.popBackStack() }
            // )
            PlaceholderScreen("Negocio: $businessId", onBack = { navController.popBackStack() })
        }
        
        // Favorites
        composable(
            route = ScreenV2.Favorites.route,
            enterTransition = { RixyNavigationTransitions.fadeEnter(this) },
            exitTransition = { RixyNavigationTransitions.fadeExit(this) }
        ) {
            PlaceholderScreen("Favoritos", onBack = { navController.popBackStack() })
        }
        
        // Orders
        composable(
            route = ScreenV2.Orders.route,
            enterTransition = { RixyNavigationTransitions.fadeEnter(this) },
            exitTransition = { RixyNavigationTransitions.fadeExit(this) }
        ) {
            PlaceholderScreen("Pedidos", onBack = { navController.popBackStack() })
        }
        
        // Profile
        composable(
            route = ScreenV2.Profile.route,
            enterTransition = { RixyNavigationTransitions.fadeEnter(this) },
            exitTransition = { RixyNavigationTransitions.fadeExit(this) }
        ) {
            PlaceholderScreen("Perfil", onBack = { navController.popBackStack() })
        }
        
        // Settings
        composable(
            route = ScreenV2.Settings.route
        ) {
            PlaceholderScreen("Ajustes", onBack = { navController.popBackStack() })
        }
        
        // Login (Modal presentation)
        composable(
            route = ScreenV2.Login.route,
            enterTransition = { RixyNavigationTransitions.modalEnter(this) },
            exitTransition = { RixyNavigationTransitions.modalExit(this) },
            popEnterTransition = { RixyNavigationTransitions.modalEnter(this) },
            popExitTransition = { RixyNavigationTransitions.modalExit(this) }
        ) {
            PlaceholderScreen("Login", onBack = { navController.popBackStack() })
        }
        
        // Register (Modal presentation)
        composable(
            route = ScreenV2.Register.route,
            enterTransition = { RixyNavigationTransitions.modalEnter(this) },
            exitTransition = { RixyNavigationTransitions.modalExit(this) },
            popEnterTransition = { RixyNavigationTransitions.modalEnter(this) },
            popExitTransition = { RixyNavigationTransitions.modalExit(this) }
        ) {
            PlaceholderScreen("Registro", onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Placeholder for screens not yet implemented
 */
@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = title,
            style = com.externalpods.rixy.core.designsystem.theme.RixyTypography.H2
        )
    }
}

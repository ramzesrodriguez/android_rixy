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
import com.externalpods.rixy.feature.user.cityhome.CityHomeScreen

/**
 * Navigation routes sealed class (type-safe)
 */
sealed class NavScreen(val route: String) {
    data object CityHome : NavScreen("city_home")
    data object Browse : NavScreen("browse")
    data object ListingDetail : NavScreen("listing_detail/{listingId}") {
        fun createRoute(listingId: String) = "listing_detail/$listingId"
    }
    data object BusinessProfile : NavScreen("business/{businessId}") {
        fun createRoute(businessId: String) = "business/$businessId"
    }
    data object Favorites : NavScreen("favorites")
    data object Orders : NavScreen("orders")
    data object Profile : NavScreen("profile")
    data object Settings : NavScreen("settings")
    data object Login : NavScreen("login")
    data object Register : NavScreen("register")
}

/**
 * RixyNavHost - Navigation with iOS-style transitions
 * 
 * Features:
 * - Push/Pop transitions (slide from right)
 * - Modal transitions (slide from bottom)
 * - Fade transitions for tabs
 * - No transition for same-level navigation
 */
@Composable
fun RixyNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavScreen.CityHome.route,
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
            route = NavScreen.CityHome.route,
            enterTransition = { EnterTransition.None }, // No animation for start destination
            exitTransition = { ExitTransition.None }
        ) {
            CityHomeScreen(
                city = city,
                onListingClick = { listing ->
                    navController.navigate(NavScreen.ListingDetail.createRoute(listing.id))
                },
                onSeeAllListings = {
                    navController.navigate(NavScreen.Browse.route)
                },
                onChangeCity = {
                    // Navigate to city selector
                    // navController.navigate(NavScreen.CitySelector.route)
                },
                onBusinessCTAClick = {
                    navController.navigate(NavScreen.Login.route)
                }
            )
        }
        
        // Browse / Search
        composable(
            route = NavScreen.Browse.route
        ) {
            // BrowseListingsScreen(
            //     cityId = city.id,
            //     onBackClick = { navController.popBackStack() },
            //     onListingClick = { listing ->
            //         navController.navigate(NavScreen.ListingDetail.createRoute(listing.id))
            //     }
            // )
            PlaceholderScreen("Buscar", onBack = { navController.popBackStack() })
        }
        
        // Listing Detail
        composable(
            route = NavScreen.ListingDetail.route
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")
            // ListingDetailScreen(
            //     listingId = listingId,
            //     onBackClick = { navController.popBackStack() },
            //     onBusinessClick = { businessId ->
            //         navController.navigate(NavScreen.BusinessProfile.createRoute(businessId))
            //     }
            // )
            PlaceholderScreen("Detalle: $listingId", onBack = { navController.popBackStack() })
        }
        
        // Business Profile
        composable(
            route = NavScreen.BusinessProfile.route
        ) { backStackEntry ->
            val businessId = backStackEntry.arguments?.getString("businessId")
            // BusinessProfileScreen(
            //     businessId = businessId,
            //     onBackClick = { navController.popBackStack() }
            // )
            PlaceholderScreen("Negocio: $businessId", onBack = { navController.popBackStack() })
        }
        
        // Favorites
        composable(
            route = NavScreen.Favorites.route,
            enterTransition = { RixyNavigationTransitions.fadeEnter(this) },
            exitTransition = { RixyNavigationTransitions.fadeExit(this) }
        ) {
            PlaceholderScreen("Favoritos", onBack = { navController.popBackStack() })
        }
        
        // Orders
        composable(
            route = NavScreen.Orders.route,
            enterTransition = { RixyNavigationTransitions.fadeEnter(this) },
            exitTransition = { RixyNavigationTransitions.fadeExit(this) }
        ) {
            PlaceholderScreen("Pedidos", onBack = { navController.popBackStack() })
        }
        
        // Profile
        composable(
            route = NavScreen.Profile.route,
            enterTransition = { RixyNavigationTransitions.fadeEnter(this) },
            exitTransition = { RixyNavigationTransitions.fadeExit(this) }
        ) {
            PlaceholderScreen("Perfil", onBack = { navController.popBackStack() })
        }
        
        // Settings
        composable(
            route = NavScreen.Settings.route
        ) {
            PlaceholderScreen("Ajustes", onBack = { navController.popBackStack() })
        }
        
        // Login (Modal presentation)
        composable(
            route = NavScreen.Login.route,
            enterTransition = { RixyNavigationTransitions.modalEnter(this) },
            exitTransition = { RixyNavigationTransitions.modalExit(this) },
            popEnterTransition = { RixyNavigationTransitions.modalEnter(this) },
            popExitTransition = { RixyNavigationTransitions.modalExit(this) }
        ) {
            PlaceholderScreen("Login", onBack = { navController.popBackStack() })
        }
        
        // Register (Modal presentation)
        composable(
            route = NavScreen.Register.route,
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

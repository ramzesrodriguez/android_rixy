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
import com.externalpods.rixy.feature.admin.audit.AuditLogsScreen
import com.externalpods.rixy.feature.admin.cities.CitiesManagementScreen
import com.externalpods.rixy.feature.admin.dashboard.AdminDashboardScreen
import com.externalpods.rixy.feature.admin.moderation.ModerationBusinessesScreen
import com.externalpods.rixy.feature.admin.moderation.ModerationListingsScreen
import com.externalpods.rixy.feature.admin.users.UsersManagementScreen
import com.externalpods.rixy.feature.auth.LoginScreen
import com.externalpods.rixy.feature.auth.RegisterScreen
import com.externalpods.rixy.feature.owner.business.BusinessEditorScreen
import com.externalpods.rixy.feature.owner.cityslots.OwnerCitySlotsScreen
import com.externalpods.rixy.feature.owner.dashboard.OwnerDashboardScreen
import com.externalpods.rixy.feature.owner.featured.FeaturedCampaignsScreen
import com.externalpods.rixy.feature.owner.listings.ListingEditorScreen
import com.externalpods.rixy.feature.user.cityselector.CitySelectorScreen
import com.externalpods.rixy.feature.user.listingdetail.ListingDetailScreen
import com.externalpods.rixy.feature.user.main.UserTabBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun RixyNavGraph(
    appState: AppState = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val currentMode by appState.currentMode.collectAsStateWithLifecycle()
    val isAuthenticated by appState.isAuthenticated.collectAsStateWithLifecycle()
    val selectedCity by appState.selectedCity.collectAsStateWithLifecycle()
    
    val startDestination = when {
        !isAuthenticated -> Screen.Login
        selectedCity == null -> Screen.CitySelector
        else -> when (currentMode) {
            AppMode.USER -> Screen.UserMain
            AppMode.OWNER -> Screen.OwnerDashboard
            AppMode.ADMIN -> Screen.AdminDashboard
            else -> Screen.CitySelector
        }
    }
    
    NavHost(navController = navController, startDestination = startDestination) {
        // Auth Routes
        composable<Screen.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register) },
                onLoginSuccess = { navController.navigate(Screen.CitySelector) { popUpTo(Screen.Login) { inclusive = true } } }
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate(Screen.CitySelector) { popUpTo(Screen.Login) { inclusive = true } } }
            )
        }
        
        // User Routes
        composable<Screen.CitySelector> {
            CitySelectorScreen(
                onCitySelected = { city ->
                    appState.selectCity(city)
                    navController.navigate(Screen.UserMain) { popUpTo(Screen.CitySelector) { inclusive = true } }
                }
            )
        }
        composable<Screen.UserMain> {
            val city = selectedCity ?: City(id = "", name = "Ciudad", slug = "")
            UserTabBar(
                city = city,
                onListingClick = { navController.navigate(Screen.ListingDetail(it.id)) },
                onNavigateToLogin = { navController.navigate(Screen.Login) { popUpTo(0) { inclusive = true } } },
                onModeChanged = { navController.navigate(Screen.OwnerDashboard) { popUpTo(0) { inclusive = true } } }
            )
        }
        composable<Screen.ListingDetail> { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            ListingDetailScreen(
                listingId = listingId,
                onBackClick = { navController.popBackStack() },
                onBusinessClick = {}
            )
        }
        
        // Owner Routes
        composable<Screen.OwnerDashboard> {
            OwnerDashboardScreen(
                onNavigateToBusiness = { navController.navigate(Screen.BusinessEditor()) },
                onNavigateToListings = { navController.navigate(Screen.ListingEditor()) },
                onNavigateToFeatured = { navController.navigate(Screen.FeaturedCampaigns) },
                onNavigateToCitySlots = { navController.navigate(Screen.CitySlots) },
                onNavigateToCreateListing = { navController.navigate(Screen.ListingEditor()) }
            )
        }
        composable<Screen.BusinessEditor> {
            BusinessEditorScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable<Screen.ListingEditor> { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")
            ListingEditorScreen(
                listingId = listingId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable<Screen.FeaturedCampaigns> {
            FeaturedCampaignsScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Screen.CitySlots> {
            OwnerCitySlotsScreen(onBackClick = { navController.popBackStack() })
        }
        
        // Admin Routes
        composable<Screen.AdminDashboard> {
            AdminDashboardScreen(
                onNavigateToModeration = { navController.navigate(Screen.Moderation) },
                onNavigateToCities = { navController.navigate(Screen.CitiesManagement) },
                onNavigateToUsers = { navController.navigate(Screen.UsersManagement) },
                onNavigateToAudit = { navController.navigate(Screen.AuditLogs) }
            )
        }
        composable<Screen.Moderation> {
            ModerationListingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Screen.CitiesManagement> {
            CitiesManagementScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Screen.UsersManagement> {
            UsersManagementScreen(onBackClick = { navController.popBackStack() })
        }
        composable<Screen.AuditLogs> {
            AuditLogsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

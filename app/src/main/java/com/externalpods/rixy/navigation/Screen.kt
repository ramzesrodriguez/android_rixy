package com.externalpods.rixy.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation routes for Rixy app
 * Uses Kotlin Serialization for type-safe navigation
 */
object Routes {
    // Auth
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    
    // User Mode
    const val USER = "user"
    const val CITY_SELECTOR = "city_selector"
    const val CITY_HOME = "city_home"
    const val LISTING_DETAIL = "listing_detail"
    const val BUSINESS_PROFILE = "business_profile"
    const val BROWSE = "browse"
    
    // Owner Mode
    const val OWNER = "owner"
    const val OWNER_DASHBOARD = "owner_dashboard"
    const val BUSINESS_EDITOR = "business_editor"
    const val LISTING_EDITOR = "listing_editor"
    const val FEATURED_CAMPAIGNS = "featured_campaigns"
    const val CITY_SLOTS = "city_slots"
    
    // Admin Mode
    const val ADMIN = "admin"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CITIES_MANAGEMENT = "cities_management"
    const val MODERATION = "moderation"
    const val USERS_MANAGEMENT = "users_management"
    const val AUDIT_LOGS = "audit_logs"
    
    // Common
    const val SETTINGS = "settings"
}

/**
 * Sealed class representing all screens in the app
 */
@Serializable
sealed class Screen {
    
    // Auth Screens
    @Serializable
    data object Login : Screen()
    
    @Serializable
    data object Register : Screen()
    
    // User Mode Screens
    @Serializable
    data object CitySelector : Screen()
    
    @Serializable
    data class CityHome(val cityId: String, val citySlug: String) : Screen()
    
    @Serializable
    data class ListingDetail(val listingId: String) : Screen()
    
    @Serializable
    data class BusinessProfile(val businessId: String) : Screen()
    
    @Serializable
    data class Browse(
        val cityId: String? = null,
        val type: String? = null,
        val category: String? = null
    ) : Screen()
    
    // Owner Mode Screens
    @Serializable
    data object OwnerDashboard : Screen()
    
    @Serializable
    data class BusinessEditor(val businessId: String? = null) : Screen()
    
    @Serializable
    data class ListingEditor(
        val listingId: String? = null,
        val businessId: String? = null
    ) : Screen()
    
    @Serializable
    data object FeaturedCampaigns : Screen()
    
    @Serializable
    data object CitySlots : Screen()
    
    // Admin Mode Screens
    @Serializable
    data object AdminDashboard : Screen()
    
    @Serializable
    data object CitiesManagement : Screen()
    
    @Serializable
    data object Moderation : Screen()
    
    @Serializable
    data object UsersManagement : Screen()
    
    @Serializable
    data object AuditLogs : Screen()
    
    // Common Screens
    @Serializable
    data object Settings : Screen()
}

/**
 * Bottom navigation items for User mode
 */
enum class UserBottomNavItem(
    val route: String,
    val label: String,
    val icon: String // Using string for icon name, will map to actual icons in composable
) {
    HOME("home", "Inicio", "home"),
    SEARCH("search", "Buscar", "search"),
    FAVORITES("favorites", "Favoritos", "favorite"),
    SETTINGS("settings", "Ajustes", "settings")
}

/**
 * Navigation drawer items for Admin mode
 */
enum class AdminDrawerItem(
    val screen: Screen,
    val label: String,
    val icon: String
) {
    DASHBOARD(Screen.AdminDashboard, "Dashboard", "dashboard"),
    CITIES(Screen.CitiesManagement, "Ciudades", "location_city"),
    MODERATION(Screen.Moderation, "Moderación", "gavel"),
    USERS(Screen.UsersManagement, "Usuarios", "people"),
    AUDIT(Screen.AuditLogs, "Auditoría", "history"),
    SETTINGS(Screen.Settings, "Ajustes", "settings")
}

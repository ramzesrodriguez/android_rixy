# Rixy Android App - Architecture Document

> **Version:** 1.0  
> **Date:** 2026-02-19  
> **For:** AI Agents (Cursor, Claude, Copilot)  
> **Goal:** Create native Android app 100% functionally equivalent to the web app

---

## 1. OVERVIEW

### 1.1 Purpose
Rixy is a local marketplace connecting local businesses with consumers. The Android app must replicate ALL web app functionality with a premium native experience following Material3 design principles.

### 1.2 Dual Functionality from Settings
The app allows switching between 3 roles from Settings (simulating different "views" or "modes"):

| Role | Description | Access |
|------|-------------|--------|
| **USER** | Public user - browse, search, buy | No login / With login |
| **OWNER** | Seller - manages business and listings | Requires auth + OWNER role |
| **ADMIN** | Platform operator - moderation, cities, pricing | Requires auth + ADMIN role |

**IMPORTANT:** In Settings there's a toggle/selection to switch between view modes (useful for development/demo). In real production, the role comes from the backend.

### 1.3 Recommended Tech Stack

```
Language: Kotlin 2.0+
UI Framework: Jetpack Compose (Material3)
Architecture: MVVM + Clean Architecture + Repository Pattern
Dependencies:
  - Supabase Kotlin Client (Auth)
  - Retrofit + OkHttp (HTTP Client)
  - Coil (Image loading and caching)
  - Stripe Android SDK (Payments)
  - Kotlinx Serialization (JSON)
  - Koin (Dependency Injection)
  - Jetpack Navigation Compose
  - DataStore (Preferences)
  - Room (Local caching - optional)
```

---

## 2. DATA MODELS

### 2.1 Main Enums (Kotlin sealed classes/enum classes)

```kotlin
// MARK: - Enums
enum class OwnerRole {
    OWNER, ADMIN
}

enum class OwnerStatus {
    ACTIVE, SUSPENDED, BANNED
}

enum class BusinessStatus {
    PENDING_REVIEW, APPROVED, REJECTED, SUSPENDED
}

enum class ListingType(val icon: String, val color: String) {
    PRODUCT("🛍️", "blue"),
    SERVICE("🔧", "purple"),
    EVENT("🎉", "orange")
}

enum class ListingStatus {
    DRAFT, PENDING_REVIEW, PUBLISHED, REJECTED, SUSPENDED
}

enum class PriceType {
    FIXED, FROM, RANGE, FREE
}

enum class StockStatus {
    IN_STOCK, LIMITED, OUT_OF_STOCK, PREORDER
}

enum class Condition {
    NEW, USED, REFURBISHED
}

enum class PricingModel {
    FIXED, FROM, HOURLY, QUOTE
}

enum class ServiceAreaType {
    ON_SITE, REMOTE, BOTH
}

enum class EventStatus {
    SCHEDULED, CANCELED, POSTPONED
}

enum class FeaturedPlacementStatus {
    PENDING, ACTIVE, EXPIRED, CANCELED
}

enum class CitySlotStatus {
    PENDING, ACTIVE, EXPIRED, CANCELED, PAUSED
}

enum class CitySlotType {
    HOME_HERO_SPOTLIGHT,
    HOME_HORIZONTAL_CAROUSEL,
    HOME_CATEGORY_RAIL,
    HOME_GRID_1,
    HOME_GRID_2,
    HOME_EVENTS_STRIP,
    HOME_NEW_ARRIVALS,
    HOME_FEATURED_PLACEMENT
}

enum class PaymentStatus {
    PENDING, PAID, FAILED, CANCELED
}

enum class CitySectionType {
    CITY_HERO_SPOTLIGHT,
    CITY_CATEGORY_RAIL,
    CITY_TOP_BUSINESSES,
    CITY_DEALS_GRID,
    CITY_EVENTS_STRIP,
    CITY_NEW_ARRIVALS,
    CITY_FEATURED_PLACEMENT
}

enum class BusinessSectionType {
    HORIZONTAL_CAROUSEL,
    GRID,
    FEATURE_CARD,
    PROMO_BANNER
}

enum class ModerationAction {
    APPROVE, REJECT, SUSPEND
}
```

### 2.2 Main Models (Kotlin data classes with kotlinx.serialization)

```kotlin
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.Instant

// MARK: - City
@Serializable
data class City(
    val id: String,
    val name: String,
    val slug: String,
    val isActive: Boolean,
    val isPublishingEnabled: Boolean,
    val isAdsEnabled: Boolean,
    val timezone: String,
    val country: String? = null,
    val state: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

// MARK: - Owner (User)
@Serializable
data class Owner(
    val id: String,
    val email: String,
    val role: OwnerRole,
    val status: OwnerStatus,
    val sessionVersion: Int,
    val createdAt: Instant,
    val business: Business? = null
)

// MARK: - Business
@Serializable
data class Business(
    val id: String,
    val ownerId: String,
    val cityId: String,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    val logoUrl: String? = null,
    val addressText: String? = null,
    val mapUrl: String? = null,
    val openingHoursText: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val website: String? = null,
    val status: BusinessStatus,
    val moderationReasonCode: String? = null,
    val moderationNote: String? = null,
    val moderatedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

// MARK: - Listing (Product/Service/Event)
@Serializable
data class Listing(
    val id: String,
    val cityId: String,
    val businessId: String,
    val ownerId: String,
    val type: ListingType,
    val title: String,
    val slug: String? = null,
    val description: String? = null,
    val categoryTag: String? = null,
    val photoUrls: List<String>,
    val isActive: Boolean,
    val status: ListingStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    
    // Type-specific details
    val productDetails: ProductDetails? = null,
    val serviceDetails: ServiceDetails? = null,
    val eventDetails: EventDetails? = null,
    
    // Nested data
    val business: BusinessSummary? = null
)

@Serializable
data class BusinessSummary(
    val id: String,
    val name: String,
    val logoUrl: String? = null
)

// MARK: - Product Details
@Serializable
data class ProductDetails(
    val listingId: String,
    val priceAmount: BigDecimal,
    val currency: String,
    val priceType: PriceType,
    val compareAtPriceAmount: BigDecimal? = null,
    val stockStatus: StockStatus,
    val stockQuantity: Int? = null,
    val sku: String? = null,
    val brand: String? = null,
    val condition: Condition,
    val attributes: ProductAttributes? = null,
    val deliveryOptions: DeliveryOptions
)

@Serializable
data class ProductAttributes(
    val colors: List<String>? = null,
    val sizes: List<String>? = null,
    val material: String? = null
)

@Serializable
data class DeliveryOptions(
    val pickup: Boolean,
    val delivery: Boolean,
    val shipping: Boolean
)

// MARK: - Service Details
@Serializable
data class ServiceDetails(
    val listingId: String,
    val pricingModel: PricingModel,
    val priceAmount: BigDecimal? = null,
    val currency: String? = null,
    val durationMinutes: Int? = null,
    val serviceAreaType: ServiceAreaType,
    val serviceAreaText: String? = null,
    val availabilityText: String? = null,
    val bookingUrl: String? = null,
    val requirements: String? = null
)

// MARK: - Event Details
@Serializable
data class EventDetails(
    val listingId: String,
    val startAt: Instant,
    val endAt: Instant? = null,
    val timezone: String,
    val venueName: String? = null,
    val addressText: String? = null,
    val mapUrl: String? = null,
    val ticketUrl: String? = null,
    val priceAmount: BigDecimal? = null,
    val currency: String? = null,
    val capacity: Int? = null,
    val isOnline: Boolean,
    val onlineUrl: String? = null,
    val ageRestriction: String? = null,
    val eventStatus: EventStatus
)

// MARK: - Featured Placement
@Serializable
data class FeaturedPlacement(
    val id: String,
    val cityId: String,
    val listingId: String,
    val businessId: String,
    val startAt: Instant,
    val endAt: Instant,
    val status: FeaturedPlacementStatus,
    val createdAt: Instant,
    
    // Nested
    val listing: ListingSummary? = null,
    val city: CitySummary? = null,
    val queuePosition: Int? = null
)

@Serializable
data class ListingSummary(
    val id: String,
    val title: String,
    val type: ListingType,
    val photoUrls: List<String>
)

@Serializable
data class CitySummary(
    val id: String,
    val name: String,
    val slug: String
)

// MARK: - City Section (Homepage sections)
@Serializable
data class CitySection(
    val id: String,
    val cityId: String,
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val type: CitySectionType,
    val order: Int,
    val isActive: Boolean,
    val configJson: Map<String, JsonElement>? = null
)

// MARK: - Business Section
@Serializable
data class BusinessSection(
    val id: String,
    val businessId: String,
    val cityId: String,
    val title: String,
    val subtitle: String? = null,
    val type: BusinessSectionType,
    val entityType: String,
    val itemIds: List<String>,
    val order: Int,
    val isActive: Boolean,
    val isPremium: Boolean
)

// MARK: - City Slot
@Serializable
data class CitySlotSubscription(
    val id: String,
    val cityId: String,
    val ownerId: String,
    val businessId: String,
    val slotType: CitySlotType,
    val status: CitySlotStatus,
    val slotIndex: Int,
    val startAt: Instant,
    val endAt: Instant,
    val amountCents: Int,
    val currency: String,
    val createdAt: Instant,
    
    val city: CitySummary? = null,
    val business: BusinessSummary? = null,
    val assignment: CitySlotAssignment? = null
)

@Serializable
data class CitySlotAssignment(
    val id: String,
    val cityId: String,
    val subscriptionId: String,
    val listingId: String,
    val slotType: CitySlotType,
    val slotIndex: Int,
    val isActive: Boolean,
    val clicksCount: Int,
    val viewsCount: Int,
    
    val listing: ListingSummary? = null
)

// MARK: - Analytics
@Serializable
data class OwnerAnalyticsOverview(
    val rangeDays: Int,
    val totals: AnalyticsTotals,
    val listingViewsByType: ListingViewsByType,
    val topListings: List<TopListing>
)

@Serializable
data class AnalyticsTotals(
    val businessViews: Int,
    val listingViews: Int,
    val totalViews: Int,
    val uniqueVisitors: Int,
    val returningVisitors: Int,
    val avgDwellMs: Int
)

@Serializable
data class ListingViewsByType(
    val product: Int,
    val service: Int,
    val event: Int
)

@Serializable
data class TopListing(
    val id: String,
    val title: String,
    val type: ListingType,
    val status: ListingStatus,
    val views: Int
)

// MARK: - Audit Log
@Serializable
data class AuditLog(
    val id: String,
    val actorOwnerId: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val cityId: String? = null,
    val reasonCode: String? = null,
    val note: String? = null,
    val beforeJson: Map<String, JsonElement>? = null,
    val afterJson: Map<String, JsonElement>? = null,
    val createdAt: Instant
)

// MARK: - API Response Wrappers
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val success: Boolean? = null,
    val message: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val nextCursor: String? = null,
    val pagination: PaginationInfo? = null
)

@Serializable
data class PaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int
)

// MARK: - City Home Response
@Serializable
data class CityHome(
    val city: City,
    val featured: Listing? = null,
    val feed: List<Listing>,
    val sections: List<CitySection> = emptyList()
)
```

---

## 3. APIS - COMPLETE ENDPOINTS

### 3.1 Base URL and Configuration

```kotlin
object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:3000/api/v1/" // Dev (Android emulator)
    // const val BASE_URL = "https://api.Rixy.app/api/v1/" // Prod
    
    var authToken: String? = null
}
```

### 3.2 API Service Interfaces (Retrofit)

```kotlin
import retrofit2.http.*
import retrofit2.Response

// MARK: - Public API
interface PublicApiService {
    
    @GET("cities")
    suspend fun getCities(
        @Query("activeOnly") activeOnly: Boolean = true
    ): Response<ApiResponse<List<City>>>
    
    @GET("{citySlug}/home")
    suspend fun getCityHome(
        @Path("citySlug") citySlug: String
    ): Response<CityHome>
    
    @GET("{citySlug}/info")
    suspend fun getCityInfo(
        @Path("citySlug") citySlug: String
    ): Response<ApiResponse<City>>
    
    @GET("{citySlug}/listings")
    suspend fun getListings(
        @Path("citySlug") citySlug: String,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("cursor") cursor: String? = null
    ): Response<PaginatedResponse<Listing>>
    
    @GET("{citySlug}/listings/{listingId}")
    suspend fun getListingDetail(
        @Path("citySlug") citySlug: String,
        @Path("listingId") listingId: String
    ): Response<ApiResponse<Listing>>
    
    @GET("{citySlug}/businesses")
    suspend fun getBusinesses(
        @Path("citySlug") citySlug: String,
        @Query("search") search: String? = null,
        @Query("cursor") cursor: String? = null
    ): Response<PaginatedResponse<Business>>
    
    @GET("{citySlug}/businesses/{businessId}")
    suspend fun getBusinessDetail(
        @Path("citySlug") citySlug: String,
        @Path("businessId") businessId: String
    ): Response<ApiResponse<Business>>
    
    @GET("{citySlug}/businesses/{businessId}/listings")
    suspend fun getBusinessListings(
        @Path("citySlug") citySlug: String,
        @Path("businessId") businessId: String,
        @Query("cursor") cursor: String? = null
    ): Response<PaginatedResponse<Listing>>
    
    @GET("{citySlug}/slots")
    suspend fun getCitySlots(
        @Path("citySlug") citySlug: String
    ): Response<ApiResponse<List<CitySlotAssignment>>>
    
    @POST("{citySlug}/analytics/view")
    suspend fun trackView(
        @Path("citySlug") citySlug: String,
        @Body body: TrackViewRequest
    ): Response<ApiResponse<Unit>>
}

// MARK: - Owner API
interface OwnerApiService {
    
    @GET("owner/me")
    suspend fun getMe(): Response<ApiResponse<Owner>>
    
    @GET("owner/analytics/overview")
    suspend fun getAnalyticsOverview(
        @Query("days") days: Int = 30
    ): Response<ApiResponse<OwnerAnalyticsOverview>>
    
    // Business
    @GET("owner/business")
    suspend fun getBusiness(): Response<ApiResponse<Business?>>
    
    @POST("owner/business")
    suspend fun createBusiness(
        @Body request: CreateBusinessRequest
    ): Response<ApiResponse<Business>>
    
    @PUT("owner/business")
    suspend fun updateBusiness(
        @Body request: UpdateBusinessRequest
    ): Response<ApiResponse<Business>>
    
    // Listings
    @GET("owner/listings")
    suspend fun getListings(): Response<ApiResponse<List<Listing>>>
    
    @POST("owner/listings")
    suspend fun createListing(
        @Body request: CreateListingRequest
    ): Response<ApiResponse<Listing>>
    
    @GET("owner/listings/{listingId}")
    suspend fun getListing(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<Listing>>
    
    @PUT("owner/listings/{listingId}")
    suspend fun updateListing(
        @Path("listingId") listingId: String,
        @Body request: UpdateListingRequest
    ): Response<ApiResponse<Listing>>
    
    @DELETE("owner/listings/{listingId}")
    suspend fun deleteListing(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<Unit>>
    
    // Uploads
    @POST("owner/uploads/presign")
    suspend fun presignUpload(
        @Body request: PresignUploadRequest
    ): Response<ApiResponse<PresignUploadResponse>>
    
    // Featured
    @GET("owner/featured")
    suspend fun getFeaturedPlacements(): Response<ApiResponse<List<FeaturedPlacement>>>
    
    @POST("owner/featured/checkout")
    suspend fun createFeaturedCheckout(
        @Body request: FeaturedCheckoutRequest
    ): Response<ApiResponse<CheckoutResponse>>
    
    // City Slots
    @GET("owner/city-slots")
    suspend fun getCitySlots(): Response<ApiResponse<List<CitySlotSubscription>>>
    
    @POST("owner/city-slots/checkout")
    suspend fun createCitySlotCheckout(
        @Body request: CreateCitySlotCheckoutRequest
    ): Response<ApiResponse<CheckoutResponse>>
}

// MARK: - Admin API
interface AdminApiService {
    
    // Cities
    @GET("admin/cities")
    suspend fun getAllCities(): Response<ApiResponse<List<City>>>
    
    @POST("admin/cities")
    suspend fun createCity(
        @Body request: CreateCityRequest
    ): Response<ApiResponse<City>>
    
    @PUT("admin/cities/{cityId}")
    suspend fun updateCity(
        @Path("cityId") cityId: String,
        @Body request: UpdateCityRequest
    ): Response<ApiResponse<City>>
    
    // Moderation - Listings
    @GET("admin/moderation/listings")
    suspend fun getModerationListings(
        @Query("status") status: String? = null,
        @Query("cityId") cityId: String? = null,
        @Query("type") type: String? = null
    ): Response<PaginatedResponse<Listing>>
    
    @POST("admin/moderation/listings/{listingId}/action")
    suspend fun moderateListing(
        @Path("listingId") listingId: String,
        @Body request: ModerateRequest
    ): Response<ApiResponse<Listing>>
    
    @GET("admin/moderation/listings/pending/count")
    suspend fun getPendingListingsCount(): Response<ApiResponse<Map<String, Int>>>
    
    // Users
    @GET("admin/users")
    suspend fun getUsers(): Response<ApiResponse<List<Owner>>>
    
    @PUT("admin/users/{userId}/role")
    suspend fun updateUserRole(
        @Path("userId") userId: String,
        @Body request: UpdateRoleRequest
    ): Response<ApiResponse<Owner>>
    
    // Audit
    @GET("admin/audit")
    suspend fun getAuditLogs(
        @Query("entityType") entityType: String? = null,
        @Query("entityId") entityId: String? = null
    ): Response<ApiResponse<List<AuditLog>>>
}

// MARK: - Request/Response Data Classes

@Serializable
data class TrackViewRequest(
    val entityType: String,
    val entityId: String,
    val action: String = "VIEW"
)

@Serializable
data class CreateBusinessRequest(
    val cityId: String,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    val logoUrl: String? = null,
    val addressText: String? = null,
    val mapUrl: String? = null,
    val openingHoursText: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val website: String? = null
)

@Serializable
data class UpdateBusinessRequest(
    val name: String? = null,
    val slug: String? = null,
    val description: String? = null,
    val logoUrl: String? = null,
    val addressText: String? = null,
    val mapUrl: String? = null,
    val openingHoursText: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val website: String? = null
)

@Serializable
data class CreateListingRequest(
    val type: ListingType,
    val title: String,
    val slug: String? = null,
    val description: String? = null,
    val categoryTag: String? = null,
    val photoUrls: List<String> = emptyList(),
    val productDetails: ProductDetailsInput? = null,
    val serviceDetails: ServiceDetailsInput? = null,
    val eventDetails: EventDetailsInput? = null
)

@Serializable
data class ProductDetailsInput(
    val priceAmount: Double,
    val currency: String = "MXN",
    val priceType: PriceType = PriceType.FIXED,
    val stockStatus: StockStatus = StockStatus.IN_STOCK,
    val deliveryOptions: DeliveryOptions
)

@Serializable
data class ServiceDetailsInput(
    val pricingModel: PricingModel,
    val priceAmount: Double? = null,
    val durationMinutes: Int? = null,
    val serviceAreaType: ServiceAreaType = ServiceAreaType.ON_SITE
)

@Serializable
data class EventDetailsInput(
    val startAt: Instant,
    val endAt: Instant? = null,
    val venueName: String? = null,
    val priceAmount: Double? = null,
    val capacity: Int? = null
)

@Serializable
data class UpdateListingRequest(
    val title: String? = null,
    val slug: String? = null,
    val description: String? = null,
    val categoryTag: String? = null,
    val photoUrls: List<String>? = null,
    val productDetails: ProductDetailsInput? = null,
    val serviceDetails: ServiceDetailsInput? = null,
    val eventDetails: EventDetailsInput? = null
)

@Serializable
data class PresignUploadRequest(
    val filename: String,
    val contentType: String
)

@Serializable
data class PresignUploadResponse(
    val uploadUrl: String,
    val publicUrl: String,
    val fields: Map<String, String>? = null
)

@Serializable
data class FeaturedCheckoutRequest(
    val listingId: String
)

@Serializable
data class CreateCitySlotCheckoutRequest(
    val cityId: String,
    val slotType: CitySlotType,
    val slotIndex: Int,
    val listingId: String
)

@Serializable
data class CheckoutResponse(
    val clientSecret: String,
    val sessionId: String
)

@Serializable
data class CreateCityRequest(
    val name: String,
    val slug: String,
    val timezone: String = "America/Mexico_City",
    val country: String? = null,
    val state: String? = null
)

@Serializable
data class UpdateCityRequest(
    val name: String? = null,
    val slug: String? = null,
    val isActive: Boolean? = null,
    val isPublishingEnabled: Boolean? = null,
    val isAdsEnabled: Boolean? = null
)

@Serializable
data class ModerateRequest(
    val action: ModerationAction,
    val reasonCode: String? = null,
    val note: String? = null
)

@Serializable
data class UpdateRoleRequest(
    val role: OwnerRole
)
```

---

## 4. DESIGN SYSTEM

### 4.1 Colors (Material3 Theme)

```kotlin
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme

object RixyColors {
    // Primary
    val Primary = Color(0xFFF97316) // Orange-500
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFFFEDD5)
    val OnPrimaryContainer = Color(0xFF7C2D12)
    
    // Secondary
    val Secondary = Color(0xFF6B7280)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFE5E7EB)
    val OnSecondaryContainer = Color(0xFF374151)
    
    // Tertiary
    val Tertiary = Color(0xFF3B82F6) // Blue
    val OnTertiary = Color(0xFFFFFFFF)
    
    // Background
    val Background = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF111827)
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF111827)
    val SurfaceVariant = Color(0xFFF3F4F6)
    
    // Status Colors
    val Success = Color(0xFF22C55E)
    val Warning = Color(0xFFF59E0B)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)
    
    // Type Colors (for badges)
    val Product = Color(0xFF3B82F6) // Blue
    val Service = Color(0xFF8B5CF6) // Purple
    val Event = Color(0xFFF97316) // Orange
}

val LightColorScheme = lightColorScheme(
    primary = RixyColors.Primary,
    onPrimary = RixyColors.OnPrimary,
    primaryContainer = RixyColors.PrimaryContainer,
    onPrimaryContainer = RixyColors.OnPrimaryContainer,
    secondary = RixyColors.Secondary,
    onSecondary = RixyColors.OnSecondary,
    secondaryContainer = RixyColors.SecondaryContainer,
    onSecondaryContainer = RixyColors.OnSecondaryContainer,
    background = RixyColors.Background,
    onBackground = RixyColors.OnBackground,
    surface = RixyColors.Surface,
    onSurface = RixyColors.OnSurface,
    surfaceVariant = RixyColors.SurfaceVariant,
    error = RixyColors.Error
)
```

### 4.2 Typography (Material3)

```kotlin
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val RixyTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 57.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontSize = 45.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
    )
)
```

### 4.3 Spacing and Shapes

```kotlin
import androidx.compose.ui.unit.dp

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object RixyShapes {
    val small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    val full = androidx.compose.foundation.shape.CircleShape
}
```

### 4.4 Main UI Components

```kotlin
// MARK: - Listing Card
@Composable
fun ListingCard(
    listing: Listing,
    citySlug: String,
    onFavorite: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    // Specs:
    // - Aspect ratio image: 4:3
    // - Corner radius: 16dp
    // - Elevation: 2dp
    // - Type badge on top-left corner
    // - Price on bottom-left (primary badge)
    // - Favorite icon on top-right
}

// MARK: - City Card
@Composable
fun CityCard(
    city: City,
    onClick: () -> Unit
) {
    // Specs:
    // - Size: adaptable, min 160dp width
    // - Background: subtle primary/5 gradient
    // - Icon: MapPin
    // - State and country below name
}

// MARK: - Status Badge
@Composable
fun StatusBadge(status: ListingStatus) {
    // Color mapping:
    // - PUBLISHED/APPROVED: Green
    // - PENDING_REVIEW: Yellow/Orange
    // - REJECTED/SUSPENDED: Red
    // - DRAFT: Gray
}

// MARK: - Section Header
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    // Specs:
    // - Title: titleLarge
    // - Subtitle: bodySmall, secondary color
    // - Action: "See more" button optional
}

// MARK: - Empty State
@Composable
fun EmptyStateView(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    action: EmptyStateAction? = null
) {
    data class EmptyStateAction(
        val title: String,
        val onClick: () -> Unit
    )
}

// MARK: - Search Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    onSearch: (() -> Unit)? = null
) {
    // Material3 SearchBar
}

// MARK: - Loading Skeleton
@Composable
fun SkeletonView(
    modifier: Modifier = Modifier
) {
    // Shimmer effect for loading states
}
```

---

## 5. SCREENS AND NAVIGATION

### 5.1 Navigation Routes

```kotlin
// Navigation routes sealed class
sealed class Screen(val route: String) {
    
    // User routes
    object CitySelector : Screen("city_selector")
    object CityHome : Screen("city_home/{citySlug}") {
        fun createRoute(citySlug: String) = "city_home/$citySlug"
    }
    object ListingDetail : Screen("listing_detail/{citySlug}/{listingId}") {
        fun createRoute(citySlug: String, listingId: String) = "listing_detail/$citySlug/$listingId"
    }
    object BusinessProfile : Screen("business_profile/{citySlug}/{businessId}") {
        fun createRoute(citySlug: String, businessId: String) = "business_profile/$citySlug/$businessId"
    }
    object BrowseListings : Screen("browse_listings/{citySlug}?type={type}") {
        fun createRoute(citySlug: String, type: String? = null) = 
            if (type != null) "browse_listings/$citySlug?type=$type" else "browse_listings/$citySlug"
    }
    object Search : Screen("search")
    
    // Owner routes
    object OwnerDashboard : Screen("owner_dashboard")
    object BusinessEditor : Screen("business_editor")
    object ListingEditor : Screen("listing_editor?listingId={listingId}") {
        fun createRoute(listingId: String? = null) = 
            if (listingId != null) "listing_editor?listingId=$listingId" else "listing_editor"
    }
    object OwnerListings : Screen("owner_listings")
    object FeaturedCampaigns : Screen("featured_campaigns")
    object OwnerCitySlots : Screen("owner_city_slots")
    
    // Admin routes
    object AdminDashboard : Screen("admin_dashboard")
    object CitiesManagement : Screen("cities_management")
    object ModerationListings : Screen("moderation_listings")
    object ModerationBusinesses : Screen("moderation_businesses")
    object AuditLogs : Screen("audit_logs")
    
    // Common
    object Settings : Screen("settings")
    object Login : Screen("login")
    object Register : Screen("register")
}
```

### 5.2 Navigation Graph

```kotlin
@Composable
fun RixyNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.CitySelector.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // User screens
        composable(Screen.CitySelector.route) {
            CitySelectorScreen(
                onCityClick = { city ->
                    navController.navigate(Screen.CityHome.createRoute(city.slug))
                }
            )
        }
        
        composable(
            route = Screen.CityHome.route,
            arguments = listOf(navArgument("citySlug") { type = NavType.StringType })
        ) { backStackEntry ->
            val citySlug = backStackEntry.arguments?.getString("citySlug")!!
            CityHomeScreen(
                citySlug = citySlug,
                onListingClick = { listing ->
                    navController.navigate(Screen.ListingDetail.createRoute(citySlug, listing.id))
                },
                onBusinessClick = { business ->
                    navController.navigate(Screen.BusinessProfile.createRoute(citySlug, business.id))
                },
                onBrowseClick = { type ->
                    navController.navigate(Screen.BrowseListings.createRoute(citySlug, type?.name))
                }
            )
        }
        
        composable(
            route = Screen.ListingDetail.route,
            arguments = listOf(
                navArgument("citySlug") { type = NavType.StringType },
                navArgument("listingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val citySlug = backStackEntry.arguments?.getString("citySlug")!!
            val listingId = backStackEntry.arguments?.getString("listingId")!!
            ListingDetailScreen(
                citySlug = citySlug,
                listingId = listingId,
                onBusinessClick = { businessId ->
                    navController.navigate(Screen.BusinessProfile.createRoute(citySlug, businessId))
                }
            )
        }
        
        // Owner screens
        composable(Screen.OwnerDashboard.route) {
            OwnerDashboardScreen(
                onCreateListing = {
                    navController.navigate(Screen.ListingEditor.createRoute())
                },
                onEditListing = { listing ->
                    navController.navigate(Screen.ListingEditor.createRoute(listing.id))
                },
                onManageBusiness = {
                    navController.navigate(Screen.BusinessEditor.route)
                },
                onFeaturedClick = {
                    navController.navigate(Screen.FeaturedCampaigns.route)
                }
            )
        }
        
        composable(Screen.BusinessEditor.route) {
            BusinessEditorScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ListingEditor.route,
            arguments = listOf(navArgument("listingId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId")
            ListingEditorScreen(
                listingId = listingId,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Admin screens
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onModerationListings = {
                    navController.navigate(Screen.ModerationListings.route)
                },
                onCitiesManagement = {
                    navController.navigate(Screen.CitiesManagement.route)
                }
            )
        }
        
        composable(Screen.ModerationListings.route) {
            ModerationListingsScreen(
                onListingClick = { listing ->
                    // Navigate to moderation detail
                }
            )
        }
        
        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
```

---

## 6. ARCHITECTURE LAYERS

### 6.1 Folder Structure

```
app/src/main/java/com/Rixy/app/
├── RixyApplication.kt
├── di/
│   ├── AppModule.kt              # Koin modules
│   └── NetworkModule.kt
├── data/
│   ├── local/
│   │   ├── DataStoreManager.kt   # Preferences
│   │   └── entity/               # Room entities (optional)
│   ├── remote/
│   │   ├── api/
│   │   │   ├── PublicApiService.kt
│   │   │   ├── OwnerApiService.kt
│   │   │   └── AdminApiService.kt
│   │   ├── dto/                  # Request/Response DTOs
│   │   └── interceptor/
│   │       └── AuthInterceptor.kt
│   └── repository/
│       ├── CityRepository.kt
│       ├── ListingRepository.kt
│       ├── BusinessRepository.kt
│       └── AuthRepository.kt
├── domain/
│   ├── model/                    # Domain models (if different from data)
│   ├── usecase/                  # Use cases (Clean Architecture)
│   │   ├── city/
│   │   ├── listing/
│   │   └── business/
│   └── repository/               # Repository interfaces
│       ├── CityRepositoryInterface.kt
│       └── ...
├── presentation/
│   ├── components/               # Reusable Compose components
│   │   ├── ListingCard.kt
│   │   ├── CityCard.kt
│   │   ├── StatusBadge.kt
│   │   └── ...
│   ├── user/
│   │   ├── cityselector/
│   │   │   ├── CitySelectorScreen.kt
│   │   │   └── CitySelectorViewModel.kt
│   │   ├── cityhome/
│   │   ├── listingdetail/
│   │   ├── businessprofile/
│   │   └── search/
│   ├── owner/
│   │   ├── dashboard/
│   │   ├── business/
│   │   ├── listings/
│   │   ├── featured/
│   │   └── cityslots/
│   ├── admin/
│   │   ├── dashboard/
│   │   ├── cities/
│   │   ├── moderation/
│   │   └── audit/
│   └── common/
│       ├── MainActivity.kt
│       ├── RixyNavGraph.kt
│       ├── theme/
│       │   ├── Color.kt
│       │   ├── Theme.kt
│       │   └── Type.kt
│       └── settings/
├── service/
│   ├── AuthService.kt
│   ├── AnalyticsService.kt
│   └── ImageUploadService.kt
└── utils/
    ├── CurrencyFormatter.kt
    ├── DateUtils.kt
    └── Result.kt                 # Sealed class for API results
```

### 6.2 MVVM with Clean Architecture

```kotlin
// MARK: - ViewModel Example
@HiltViewModel
class CityHomeViewModel @Inject constructor(
    private val getCityHomeUseCase: GetCityHomeUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CityHomeUiState>(CityHomeUiState.Loading)
    val uiState: StateFlow<CityHomeUiState> = _uiState.asStateFlow()
    
    fun loadCity(slug: String) {
        viewModelScope.launch {
            _uiState.value = CityHomeUiState.Loading
            
            getCityHomeUseCase(slug)
                .onSuccess { home ->
                    _uiState.value = CityHomeUiState.Success(home)
                }
                .onFailure { error ->
                    _uiState.value = CityHomeUiState.Error(error.message)
                }
        }
    }
}

sealed class CityHomeUiState {
    object Loading : CityHomeUiState()
    data class Success(val home: CityHome) : CityHomeUiState()
    data class Error(val message: String?) : CityHomeUiState()
}

// MARK: - Use Case
class GetCityHomeUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(slug: String): Result<CityHome> {
        return try {
            val home = cityRepository.getCityHome(slug)
            Result.success(home)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// MARK: - Repository
interface CityRepository {
    suspend fun getCities(activeOnly: Boolean): List<City>
    suspend fun getCityHome(slug: String): CityHome
}

class CityRepositoryImpl @Inject constructor(
    private val publicApiService: PublicApiService
) : CityRepository {
    
    override suspend fun getCityHome(slug: String): CityHome {
        val response = publicApiService.getCityHome(slug)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }
    
    override suspend fun getCities(activeOnly: Boolean): List<City> {
        val response = publicApiService.getCities(activeOnly)
        if (response.isSuccessful) {
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }
}
```

### 6.3 Dependency Injection (Koin)

```kotlin
// AppModule.kt
val appModule = module {
    // Network
    single { provideRetrofit() }
    single { get<Retrofit>().create(PublicApiService::class.java) }
    single { get<Retrofit>().create(OwnerApiService::class.java) }
    single { get<Retrofit>().create(AdminApiService::class.java) }
    
    // Repositories
    single<CityRepository> { CityRepositoryImpl(get()) }
    single<ListingRepository> { ListingRepositoryImpl(get(), get()) }
    single<BusinessRepository> { BusinessRepositoryImpl(get()) }
    
    // Use Cases
    factory { GetCityHomeUseCase(get()) }
    factory { GetCitiesUseCase(get()) }
    
    // ViewModels
    viewModel { CityHomeViewModel(get()) }
    viewModel { CitySelectorViewModel(get()) }
    viewModel { OwnerDashboardViewModel(get(), get(), get()) }
}

fun provideRetrofit(): Retrofit {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    
    return Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(kotlinx.serialization.json.Json.asConverterFactory("application/json".toMediaType()))
        .build()
}

// AuthInterceptor.kt
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        
        ApiConfig.authToken?.let { token ->
            request.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(request.build())
    }
}
```

---

## 7. AUTHENTICATION

### 7.1 Supabase Auth Integration

```kotlin
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth

class AuthService {
    private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://xawldrmuwlfvjucqlnyw.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    ) {
        install(Auth)
    }
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            loadUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        client.auth.signOut()
        ApiConfig.authToken = null
        _authState.value = AuthState.Unauthenticated
    }
    
    suspend fun loadUser() {
        val token = client.auth.currentSessionOrNull()?.accessToken
        ApiConfig.authToken = token
        
        // Get user profile from our API
        // GET /owner/me
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: Owner) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

### 7.2 Protected Routes

```kotlin
@Composable
fun AuthenticatedContent(
    authService: AuthService = get(),
    content: @Composable () -> Unit
) {
    val authState by authService.authState.collectAsState()
    
    when (authState) {
        is AuthState.Authenticated -> content()
        is AuthState.Unauthenticated -> LoginScreen()
        is AuthState.Loading -> CircularProgressIndicator()
        is AuthState.Error -> ErrorScreen((authState as AuthState.Error).message)
    }
}
```

---

## 8. SPECIFIC FEATURES

### 8.1 Image Upload (Presigned URLs)

```kotlin
class ImageUploadService @Inject constructor(
    private val ownerApiService: OwnerApiService
) {
    suspend fun uploadImage(uri: Uri, filename: String): Result<String> {
        return try {
            // 1. Get presigned URL
            val presignResponse = ownerApiService.presignUpload(
                PresignUploadRequest(filename, "image/jpeg")
            )
            
            if (!presignResponse.isSuccessful) {
                return Result.failure(Exception("Failed to get presigned URL"))
            }
            
            val presignData = presignResponse.body()?.data
                ?: return Result.failure(Exception("Empty presign response"))
            
            // 2. Upload to S3/R2
            val imageData = getImageBytes(uri)
            
            val uploadRequest = Request.Builder()
                .url(presignData.uploadUrl)
                .put(imageData.toRequestBody("image/jpeg".toMediaType()))
                .build()
            
            val uploadResponse = OkHttpClient().newCall(uploadRequest).execute()
            
            if (uploadResponse.isSuccessful) {
                Result.success(presignData.publicUrl)
            } else {
                Result.failure(Exception("Upload failed: ${uploadResponse.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 8.2 Stripe Payments

```kotlin
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class PaymentService @Inject constructor(
    private val ownerApiService: OwnerApiService
) {
    private var paymentSheet: PaymentSheet? = null
    
    fun initialize(activity: ComponentActivity) {
        PaymentConfiguration.init(
            activity,
            "pk_test_..." // Stripe publishable key
        )
        paymentSheet = PaymentSheet(activity) { result ->
            handlePaymentResult(result)
        }
    }
    
    suspend fun presentPaymentSheet(listingId: String): Result<Unit> {
        return try {
            // Create checkout session
            val response = ownerApiService.createFeaturedCheckout(
                FeaturedCheckoutRequest(listingId)
            )
            
            if (!response.isSuccessful) {
                return Result.failure(Exception("Failed to create checkout"))
            }
            
            val checkout = response.body()?.data
                ?: return Result.failure(Exception("Empty response"))
            
            // Present PaymentSheet
            paymentSheet?.presentWithPaymentIntent(
                paymentIntentClientSecret = checkout.clientSecret,
                configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "Rixy"
                )
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun handlePaymentResult(result: PaymentSheetResult) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                // Payment successful
            }
            is PaymentSheetResult.Canceled -> {
                // Payment canceled
            }
            is PaymentSheetResult.Failed -> {
                // Payment failed
            }
        }
    }
}
```

### 8.3 Analytics Tracking

```kotlin
class AnalyticsService @Inject constructor(
    private val publicApiService: PublicApiService
) {
    fun trackView(citySlug: String, entityType: String, entityId: String) {
        // Fire and forget
        CoroutineScope(Dispatchers.IO).launch {
            try {
                publicApiService.trackView(
                    citySlug = citySlug,
                    body = TrackViewRequest(entityType, entityId, "VIEW")
                )
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }
}
```

---

## 9. STEP-BY-STEP IMPLEMENTATION

### Phase 1: Setup and Core (Day 1-2)
1. Create Android Studio project with Jetpack Compose
2. Configure dependencies (Gradle KTS)
3. Implement data models with kotlinx.serialization
4. Implement Retrofit API interfaces
5. Configure Koin dependency injection
6. Configure Supabase Auth

### Phase 2: User Mode - Basic (Day 3-5)
1. City Selector screen
2. City Home screen with dynamic sections
3. Listing Detail screen
4. Business Profile screen
5. Browse/Search listings

### Phase 3: Owner Mode (Day 6-8)
1. Owner Dashboard
2. Business Editor
3. Listing CRUD
4. Featured Campaigns
5. City Slots

### Phase 4: Admin Mode (Day 9-10)
1. Admin Dashboard
2. Moderation queues
3. City management
4. User management
5. Audit logs

### Phase 5: Polish (Day 11-12)
1. Settings with dual mode
2. Animations and transitions
3. Error handling and empty states
4. Basic offline support (caching)
5. Testing

---

## 10. IMPLEMENTATION CHECKLIST

### User Mode
- [ ] City selector with grid
- [ ] City home with dynamic sections
- [ ] Listing cards with images and prices
- [ ] Listing detail with all info
- [ ] Business profile
- [ ] Search and filters
- [ ] Favorites (local or backend)

### Owner Mode
- [ ] Dashboard with analytics
- [ ] Business profile editor
- [ ] Listing list with status
- [ ] Listing editor (3 types)
- [ ] Image upload
- [ ] Featured campaigns
- [ ] City slots booking

### Admin Mode
- [ ] Dashboard with stats
- [ ] Moderation queues
- [ ] Approve/Reject with reason
- [ ] City CRUD
- [ ] City sections editor
- [ ] Pricing management
- [ ] Audit logs viewer

### Technical
- [ ] Auth with Supabase
- [ ] API client with auth headers
- [ ] Image caching (Coil)
- [ ] Global error handling
- [ ] Loading states
- [ ] Pull to refresh
- [ ] Pagination (cursor-based)

---

## 11. IMPORTANT NOTES

1. **Role Switching**: In Settings, allow switching between "View as User", "View as Owner", "View as Admin". This is mainly for development/demo. In real production, the role comes from the backend.

2. **Offline First**: For MVP, can be online-only. But consider caching images and city data with Coil and Room.

3. **Deep Linking**: Implement Android App Links for sharing listings and businesses:
   - `https://Rixy.app/l/{listingId}`
   - `https://Rixy.app/b/{businessId}`

4. **Push Notifications**: Prepare structure for notifications (approval, messages, etc.) using Firebase Cloud Messaging.

5. **Accessibility**: Use `Modifier.semantics()` and content descriptions on all interactive elements.

6. **Localization**: Prepare strings in Spanish and English (Mexican Spanish primary) using `res/values-es/strings.xml`.

---

**End of document. This is the complete blueprint for building the native Rixy Android app with Kotlin and Jetpack Compose.**

package com.externalpods.rixy.core.network

import com.externalpods.rixy.core.model.*
import com.externalpods.rixy.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

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

    // City Sections
    @GET("admin/city-sections")
    suspend fun getCitySections(
        @Query("cityId") cityId: String? = null
    ): Response<ApiResponse<List<CitySection>>>

    @POST("admin/city-sections")
    suspend fun createCitySection(
        @Body request: CreateCitySectionRequest
    ): Response<ApiResponse<CitySection>>

    @PUT("admin/city-sections/{sectionId}")
    suspend fun updateCitySection(
        @Path("sectionId") sectionId: String,
        @Body request: UpdateCitySectionRequest
    ): Response<ApiResponse<CitySection>>

    @DELETE("admin/city-sections/{sectionId}")
    suspend fun deleteCitySection(
        @Path("sectionId") sectionId: String
    ): Response<ApiResponse<Unit>>

    // Moderation - Listings
    @GET("admin/moderation/listings")
    suspend fun getModerationListings(
        @Query("status") status: String? = null,
        @Query("cityId") cityId: String? = null,
        @Query("type") type: String? = null
    ): Response<PaginatedResponse<Listing>>

    @GET("admin/moderation/listings/{listingId}")
    suspend fun getModerationListing(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<Listing>>

    @POST("admin/moderation/listings/{listingId}/action")
    suspend fun moderateListing(
        @Path("listingId") listingId: String,
        @Body request: ModerateRequest
    ): Response<ApiResponse<Listing>>

    @GET("admin/moderation/listings/pending/count")
    suspend fun getPendingListingsCount(): Response<ApiResponse<Map<String, Int>>>

    @GET("admin/moderation/listings/stats")
    suspend fun getListingsStats(): Response<ApiResponse<Map<String, Any>>>

    // Moderation - Businesses
    @GET("admin/moderation/businesses")
    suspend fun getModerationBusinesses(
        @Query("status") status: String? = null,
        @Query("cityId") cityId: String? = null
    ): Response<PaginatedResponse<Business>>

    @GET("admin/moderation/businesses/{businessId}")
    suspend fun getModerationBusiness(
        @Path("businessId") businessId: String
    ): Response<ApiResponse<Business>>

    @POST("admin/moderation/businesses/{businessId}/action")
    suspend fun moderateBusiness(
        @Path("businessId") businessId: String,
        @Body request: ModerateRequest
    ): Response<ApiResponse<Business>>

    @GET("admin/moderation/businesses/pending/count")
    suspend fun getPendingBusinessesCount(): Response<ApiResponse<Map<String, Int>>>

    // Users
    @GET("admin/users")
    suspend fun getUsers(): Response<ApiResponse<List<Owner>>>

    @PUT("admin/users/{userId}/role")
    suspend fun updateUserRole(
        @Path("userId") userId: String,
        @Body request: UpdateRoleRequest
    ): Response<ApiResponse<Owner>>

    // Featured
    @GET("admin/featured")
    suspend fun getAllFeaturedPlacements(): Response<ApiResponse<List<FeaturedPlacement>>>

    // Pricing
    @GET("admin/pricing")
    suspend fun getPricing(): Response<ApiResponse<List<SlotPricingItem>>>

    @PUT("admin/pricing")
    suspend fun updatePricing(
        @Body request: List<UpdatePricingRequest>
    ): Response<ApiResponse<List<SlotPricingItem>>>

    // Payments
    @GET("admin/payments")
    suspend fun getPayments(
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<PaymentTransaction>>>

    // City Slots Admin
    @GET("admin/city-slots/subscriptions")
    suspend fun getSlotSubscriptions(
        @Query("cityId") cityId: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<CitySlotSubscription>>>

    @GET("admin/city-slots/subscriptions/{subscriptionId}")
    suspend fun getSlotSubscription(
        @Path("subscriptionId") subscriptionId: String
    ): Response<ApiResponse<CitySlotSubscription>>

    @POST("admin/city-slots/{subscriptionId}/pause")
    suspend fun pauseSlotSubscription(
        @Path("subscriptionId") subscriptionId: String
    ): Response<ApiResponse<CitySlotSubscription>>

    @POST("admin/city-slots/{subscriptionId}/resume")
    suspend fun resumeSlotSubscription(
        @Path("subscriptionId") subscriptionId: String
    ): Response<ApiResponse<CitySlotSubscription>>

    @POST("admin/city-slots/{subscriptionId}/cancel")
    suspend fun cancelSlotSubscription(
        @Path("subscriptionId") subscriptionId: String,
        @Body request: CancelSlotRequest? = null
    ): Response<ApiResponse<CitySlotSubscription>>

    @GET("admin/city-slots/assignments")
    suspend fun getSlotAssignments(
        @Query("cityId") cityId: String? = null,
        @Query("slotType") slotType: String? = null
    ): Response<ApiResponse<List<PublicCitySlot>>>

    // Audit
    @GET("admin/audit")
    suspend fun getAuditLogs(
        @Query("entityType") entityType: String? = null,
        @Query("entityId") entityId: String? = null
    ): Response<ApiResponse<List<AuditLog>>>
}

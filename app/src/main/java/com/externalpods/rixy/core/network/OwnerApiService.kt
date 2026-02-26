package com.externalpods.rixy.core.network

import com.externalpods.rixy.core.model.*
import com.externalpods.rixy.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface OwnerApiService {

    // Profile
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

    // Favorites
    @GET("owner/favorites/ids")
    suspend fun getFavoriteListingIds(): Response<ApiResponse<List<String>>>

    @GET("owner/favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<Listing>>>

    @POST("owner/favorites")
    suspend fun addFavorite(
        @Body request: FavoriteRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("owner/favorites/{listingId}")
    suspend fun removeFavorite(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<Unit>>

    // Uploads
    @POST("owner/uploads/presign")
    suspend fun presignUpload(
        @Body request: PresignUploadRequest
    ): Response<ApiResponse<PresignUploadResponse>>

    // Featured Placements
    @GET("owner/featured")
    suspend fun getFeaturedPlacements(): Response<ApiResponse<List<FeaturedPlacement>>>

    @POST("owner/featured/checkout")
    suspend fun createFeaturedCheckout(
        @Body request: FeaturedCheckoutRequest
    ): Response<ApiResponse<CheckoutResponse>>

    @POST("owner/featured/{listingId}/retry")
    suspend fun retryFeaturedCheckout(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<CheckoutResponse>>

    @POST("owner/featured/{listingId}/cancel")
    suspend fun cancelFeaturedCheckout(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<Unit>>

    @POST("owner/featured/{listingId}/renew")
    suspend fun renewFeaturedCheckout(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<CheckoutResponse>>

    @POST("owner/featured/{listingId}/confirm")
    suspend fun confirmFeaturedPayment(
        @Path("listingId") listingId: String
    ): Response<ApiResponse<FeaturedPlacement>>

    // Business Sections
    @GET("owner/business-sections")
    suspend fun getBusinessSections(): Response<ApiResponse<List<BusinessSection>>>

    @POST("owner/business-sections")
    suspend fun createBusinessSection(
        @Body request: CreateBusinessSectionRequest
    ): Response<ApiResponse<BusinessSection>>

    @PUT("owner/business-sections/{sectionId}")
    suspend fun updateBusinessSection(
        @Path("sectionId") sectionId: String,
        @Body request: UpdateBusinessSectionRequest
    ): Response<ApiResponse<BusinessSection>>

    @DELETE("owner/business-sections/{sectionId}")
    suspend fun deleteBusinessSection(
        @Path("sectionId") sectionId: String
    ): Response<ApiResponse<Unit>>

    // City Slots
    @GET("owner/city-slots/availability/{citySlug}")
    suspend fun getCitySlotAvailability(
        @Path("citySlug") citySlug: String
    ): Response<ApiResponse<CitySlotAvailabilityResponse>>

    @GET("owner/city-slots/subscriptions")
    suspend fun getCitySlotSubscriptions(): Response<ApiResponse<List<CitySlotSubscription>>>

    @GET("owner/city-slots/subscriptions/history")
    suspend fun getCitySlotSubscriptionHistory(): Response<ApiResponse<List<CitySlotSubscription>>>

    @POST("owner/city-slots/checkout")
    suspend fun createCitySlotCheckout(
        @Body request: CreateCitySlotCheckoutRequest
    ): Response<ApiResponse<CitySlotCheckoutResponse>>

    @POST("owner/city-slots/subscriptions/{subscriptionId}/retry")
    suspend fun retryCitySlotPayment(
        @Path("subscriptionId") subscriptionId: String,
        @Body request: CitySlotActionRequest
    ): Response<ApiResponse<CitySlotCheckoutResponse>>

    @POST("owner/city-slots/subscriptions/{subscriptionId}/renew")
    suspend fun renewCitySlotSubscription(
        @Path("subscriptionId") subscriptionId: String,
        @Body request: CitySlotActionRequest
    ): Response<ApiResponse<CitySlotCheckoutResponse>>

    @POST("owner/city-slots/{subscriptionId}/confirm")
    suspend fun confirmCitySlotPayment(
        @Path("subscriptionId") subscriptionId: String
    ): Response<ApiResponse<CitySlotSubscription>>

    @POST("owner/city-slots/{subscriptionId}/cancel")
    suspend fun cancelCitySlot(
        @Path("subscriptionId") subscriptionId: String,
        @Body request: CancelSlotRequest? = null
    ): Response<ApiResponse<CitySlotSubscription>>
}

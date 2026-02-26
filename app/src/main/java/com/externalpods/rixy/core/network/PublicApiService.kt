package com.externalpods.rixy.core.network

import com.externalpods.rixy.core.model.*
import com.externalpods.rixy.core.network.dto.TrackViewRequest
import retrofit2.Response
import retrofit2.http.*

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

    @GET("{citySlug}/home/sections")
    suspend fun getCitySections(
        @Path("citySlug") citySlug: String
    ): Response<ApiResponse<List<CitySection>>>

    @GET("{citySlug}/home/sections/{sectionKey}/items")
    suspend fun getCitySectionItems(
        @Path("citySlug") citySlug: String,
        @Path("sectionKey") sectionKey: String,
        @Query("limit") limit: Int? = null
    ): Response<CitySectionItemsResponse>

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
    ): Response<ApiResponse<List<PublicCitySlot>>>

    @POST("{citySlug}/analytics/view")
    suspend fun trackView(
        @Path("citySlug") citySlug: String,
        @Body body: TrackViewRequest
    ): Response<ApiResponse<Unit>>

    @POST("slots/{assignmentId}/view")
    suspend fun trackSlotView(
        @Path("assignmentId") assignmentId: String
    ): Response<ApiResponse<Unit>>

    @POST("slots/{assignmentId}/click")
    suspend fun trackSlotClick(
        @Path("assignmentId") assignmentId: String
    ): Response<ApiResponse<Unit>>
}

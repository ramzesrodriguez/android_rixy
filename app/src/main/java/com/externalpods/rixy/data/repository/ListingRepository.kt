package com.externalpods.rixy.data.repository

import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.PaginatedResponse
import com.externalpods.rixy.core.network.PublicApiService

interface ListingRepository {
    suspend fun getListingsPage(
        citySlug: String,
        type: String? = null,
        category: String? = null,
        search: String? = null,
        cursor: String? = null
    ): PaginatedResponse<Listing>

    suspend fun getListings(
        citySlug: String,
        type: String? = null,
        category: String? = null,
        search: String? = null,
        cursor: String? = null
    ): List<Listing>
    
    suspend fun getListingDetail(citySlug: String, listingId: String): Listing
    suspend fun getBusinessListings(citySlug: String, businessId: String, cursor: String? = null): List<Listing>
}

class ListingRepositoryImpl(
    private val publicApi: PublicApiService
) : ListingRepository {

    override suspend fun getListingsPage(
        citySlug: String,
        type: String?,
        category: String?,
        search: String?,
        cursor: String?
    ): PaginatedResponse<Listing> {
        return try {
            val response = publicApi.getListings(citySlug, type, category, search, cursor)
            if (response.isSuccessful) {
                response.body() ?: PaginatedResponse(emptyList())
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getListings(
        citySlug: String,
        type: String?,
        category: String?,
        search: String?,
        cursor: String?
    ): List<Listing> = getListingsPage(citySlug, type, category, search, cursor).data

    override suspend fun getListingDetail(citySlug: String, listingId: String): Listing {
        return try {
            val response = publicApi.getListingDetail(citySlug, listingId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("Listing not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getBusinessListings(citySlug: String, businessId: String, cursor: String?): List<Listing> {
        return try {
            val response = publicApi.getBusinessListings(citySlug, businessId, cursor)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }
}

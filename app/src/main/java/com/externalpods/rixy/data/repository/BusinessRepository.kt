package com.externalpods.rixy.data.repository

import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.network.PublicApiService

interface BusinessRepository {
    suspend fun getBusinesses(
        citySlug: String,
        search: String? = null,
        cursor: String? = null
    ): List<Business>
    
    suspend fun getBusinessDetail(citySlug: String, businessId: String): Business
}

class BusinessRepositoryImpl(
    private val publicApi: PublicApiService
) : BusinessRepository {

    override suspend fun getBusinesses(
        citySlug: String,
        search: String?,
        cursor: String?
    ): List<Business> {
        return try {
            val response = publicApi.getBusinesses(citySlug, search, cursor)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getBusinessDetail(citySlug: String, businessId: String): Business {
        return try {
            val response = publicApi.getBusinessDetail(citySlug, businessId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("Business not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }
}

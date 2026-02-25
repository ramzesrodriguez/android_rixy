package com.externalpods.rixy.data.repository

import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.CityHome
import com.externalpods.rixy.core.model.CitySection
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.PublicCitySlot
import com.externalpods.rixy.core.network.PublicApiService

interface CityRepository {
    suspend fun getCities(activeOnly: Boolean = true): List<City>
    suspend fun getCityHome(citySlug: String): CityHome
    suspend fun getCityInfo(citySlug: String): City
    suspend fun getCitySections(citySlug: String): List<CitySection>
    suspend fun getCitySectionItems(citySlug: String, sectionKey: String, limit: Int? = null): List<Listing>
    suspend fun getCitySlots(citySlug: String): List<PublicCitySlot>
}

class CityRepositoryImpl(
    private val publicApi: PublicApiService
) : CityRepository {

    override suspend fun getCities(activeOnly: Boolean): List<City> {
        return try {
            val response = publicApi.getCities(activeOnly)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCityHome(citySlug: String): CityHome {
        return try {
            val response = publicApi.getCityHome(citySlug)
            if (response.isSuccessful) {
                response.body() ?: throw ApiError.NotFound("City home not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCityInfo(citySlug: String): City {
        return try {
            val response = publicApi.getCityInfo(citySlug)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("City not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCitySections(citySlug: String): List<CitySection> {
        return try {
            val response = publicApi.getCitySections(citySlug)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCitySectionItems(citySlug: String, sectionKey: String, limit: Int?): List<Listing> {
        return try {
            val response = publicApi.getCitySectionItems(citySlug, sectionKey, limit)
            if (response.isSuccessful) {
                response.body()?.items ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCitySlots(citySlug: String): List<PublicCitySlot> {
        return try {
            val response = publicApi.getCitySlots(citySlug)
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

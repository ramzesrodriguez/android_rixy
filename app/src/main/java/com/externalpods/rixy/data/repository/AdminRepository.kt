package com.externalpods.rixy.data.repository

import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.core.model.*
import com.externalpods.rixy.core.network.AdminApiService
import com.externalpods.rixy.core.network.dto.*

interface AdminRepository {
    // Cities
    suspend fun getAllCities(): List<City>
    suspend fun createCity(request: CreateCityRequest): City
    suspend fun updateCity(cityId: String, request: UpdateCityRequest): City
    
    // City Sections
    suspend fun getCitySections(cityId: String? = null): List<CitySection>
    suspend fun createCitySection(request: CreateCitySectionRequest): CitySection
    suspend fun updateCitySection(sectionId: String, request: UpdateCitySectionRequest): CitySection
    suspend fun deleteCitySection(sectionId: String)
    
    // Moderation - Listings
    suspend fun getModerationListings(status: String? = null, cityId: String? = null, type: String? = null): List<Listing>
    suspend fun moderateListing(listingId: String, request: ModerateRequest): Listing
    suspend fun getPendingListingsCount(): Map<String, Int>
    suspend fun getListingsStats(): Map<String, Any>
    
    // Moderation - Businesses
    suspend fun getModerationBusinesses(status: String? = null, cityId: String? = null): List<Business>
    suspend fun moderateBusiness(businessId: String, request: ModerateRequest): Business
    suspend fun getPendingBusinessesCount(): Map<String, Int>
    
    // Users
    suspend fun getUsers(): List<Owner>
    suspend fun updateUserRole(userId: String, role: OwnerRole): Owner
    
    // Featured
    suspend fun getAllFeaturedPlacements(): List<FeaturedPlacement>
    
    // Pricing
    suspend fun getPricing(): List<SlotPricingItem>
    suspend fun updatePricing(request: List<UpdatePricingRequest>): List<SlotPricingItem>
    
    // Payments
    suspend fun getPayments(status: String? = null): List<PaymentTransaction>
    
    // City Slots Admin
    suspend fun getSlotSubscriptions(cityId: String? = null, status: String? = null): List<CitySlotSubscription>
    suspend fun pauseSlotSubscription(subscriptionId: String): CitySlotSubscription
    suspend fun resumeSlotSubscription(subscriptionId: String): CitySlotSubscription
    suspend fun cancelSlotSubscription(subscriptionId: String, reasonCode: String? = null, note: String? = null): CitySlotSubscription
    
    // Audit
    suspend fun getAuditLogs(entityType: String? = null, entityId: String? = null): List<AuditLog>
}

class AdminRepositoryImpl(
    private val adminApi: AdminApiService
) : AdminRepository {

    // Cities
    override suspend fun getAllCities(): List<City> {
        return try {
            val response = adminApi.getAllCities()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createCity(request: CreateCityRequest): City {
        return try {
            val response = adminApi.createCity(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updateCity(cityId: String, request: UpdateCityRequest): City {
        return try {
            val response = adminApi.updateCity(cityId, request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // City Sections
    override suspend fun getCitySections(cityId: String?): List<CitySection> {
        return try {
            val response = adminApi.getCitySections(cityId)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createCitySection(request: CreateCitySectionRequest): CitySection {
        return try {
            val response = adminApi.createCitySection(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updateCitySection(sectionId: String, request: UpdateCitySectionRequest): CitySection {
        return try {
            val response = adminApi.updateCitySection(sectionId, request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun deleteCitySection(sectionId: String) {
        return try {
            val response = adminApi.deleteCitySection(sectionId)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Moderation - Listings
    override suspend fun getModerationListings(status: String?, cityId: String?, type: String?): List<Listing> {
        return try {
            val response = adminApi.getModerationListings(status, cityId, type)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun moderateListing(listingId: String, request: ModerateRequest): Listing {
        return try {
            val response = adminApi.moderateListing(listingId, request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getPendingListingsCount(): Map<String, Int> {
        return try {
            val response = adminApi.getPendingListingsCount()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyMap()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getListingsStats(): Map<String, Any> {
        return try {
            val response = adminApi.getListingsStats()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyMap()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Moderation - Businesses
    override suspend fun getModerationBusinesses(status: String?, cityId: String?): List<Business> {
        return try {
            val response = adminApi.getModerationBusinesses(status, cityId)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun moderateBusiness(businessId: String, request: ModerateRequest): Business {
        return try {
            val response = adminApi.moderateBusiness(businessId, request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getPendingBusinessesCount(): Map<String, Int> {
        return try {
            val response = adminApi.getPendingBusinessesCount()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyMap()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Users
    override suspend fun getUsers(): List<Owner> {
        return try {
            val response = adminApi.getUsers()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updateUserRole(userId: String, role: OwnerRole): Owner {
        return try {
            val response = adminApi.updateUserRole(userId, UpdateRoleRequest(role))
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Featured
    override suspend fun getAllFeaturedPlacements(): List<FeaturedPlacement> {
        return try {
            val response = adminApi.getAllFeaturedPlacements()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Pricing
    override suspend fun getPricing(): List<SlotPricingItem> {
        return try {
            val response = adminApi.getPricing()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updatePricing(request: List<UpdatePricingRequest>): List<SlotPricingItem> {
        return try {
            val response = adminApi.updatePricing(request)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Payments
    override suspend fun getPayments(status: String?): List<PaymentTransaction> {
        return try {
            val response = adminApi.getPayments(status)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // City Slots Admin
    override suspend fun getSlotSubscriptions(cityId: String?, status: String?): List<CitySlotSubscription> {
        return try {
            val response = adminApi.getSlotSubscriptions(cityId, status)
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun pauseSlotSubscription(subscriptionId: String): CitySlotSubscription {
        return try {
            val response = adminApi.pauseSlotSubscription(subscriptionId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun resumeSlotSubscription(subscriptionId: String): CitySlotSubscription {
        return try {
            val response = adminApi.resumeSlotSubscription(subscriptionId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun cancelSlotSubscription(
        subscriptionId: String, 
        reasonCode: String?, 
        note: String?
    ): CitySlotSubscription {
        return try {
            val response = adminApi.cancelSlotSubscription(subscriptionId, CancelSlotRequest(reasonCode, note))
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Audit
    override suspend fun getAuditLogs(entityType: String?, entityId: String?): List<AuditLog> {
        return try {
            val response = adminApi.getAuditLogs(entityType, entityId)
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

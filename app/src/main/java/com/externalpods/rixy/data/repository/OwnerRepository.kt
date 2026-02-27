package com.externalpods.rixy.data.repository

import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.core.model.*
import com.externalpods.rixy.core.network.OwnerApiService
import com.externalpods.rixy.core.network.dto.*

interface OwnerRepository {
    // Profile
    suspend fun getProfile(): Owner
    suspend fun getAnalytics(days: Int = 30): OwnerAnalyticsOverview
    
    // Business
    suspend fun getBusiness(): Business?
    suspend fun createBusiness(request: CreateBusinessRequest): Business
    suspend fun updateBusiness(request: UpdateBusinessRequest): Business
    
    // Listings
    suspend fun getListings(): List<Listing>
    suspend fun createListing(request: CreateListingRequest): Listing
    suspend fun getListing(listingId: String): Listing
    suspend fun updateListing(listingId: String, request: UpdateListingRequest): Listing
    suspend fun deleteListing(listingId: String)
    
    // Favorites
    suspend fun getFavoriteIds(): List<String>
    suspend fun getFavorites(): List<Listing> // Returns full listing objects
    suspend fun addFavorite(listingId: String)
    suspend fun removeFavorite(listingId: String)
    
    // Uploads
    suspend fun presignUpload(filename: String, contentType: String): PresignUploadResponse
    
    // Featured Placements
    suspend fun getFeaturedPlacements(): List<FeaturedPlacement>
    suspend fun createFeaturedCheckout(listingId: String): CheckoutResponse
    suspend fun retryFeaturedCheckout(listingId: String): CheckoutResponse
    suspend fun cancelFeaturedCheckout(listingId: String)
    suspend fun renewFeaturedCheckout(listingId: String): CheckoutResponse
    suspend fun confirmFeaturedPayment(listingId: String)
    
    // Business Sections
    suspend fun getBusinessSections(): List<BusinessSection>
    suspend fun createBusinessSection(request: CreateBusinessSectionRequest): BusinessSection
    suspend fun updateBusinessSection(sectionId: String, request: UpdateBusinessSectionRequest): BusinessSection
    suspend fun deleteBusinessSection(sectionId: String)
    
    // City Slots
    suspend fun getCitySlotAvailability(citySlug: String): CitySlotAvailabilityResponse
    suspend fun getCitySlotSubscriptions(): List<CitySlotSubscription>
    suspend fun getCitySlotSubscriptionHistory(): List<CitySlotSubscription>
    suspend fun createCitySlotCheckout(request: CreateCitySlotCheckoutRequest): CitySlotCheckoutResponse
    suspend fun retryCitySlotPayment(subscriptionId: String, request: CitySlotActionRequest? = null): CitySlotCheckoutResponse
    suspend fun renewCitySlotSubscription(subscriptionId: String, request: CitySlotActionRequest? = null): CitySlotCheckoutResponse
    suspend fun confirmCitySlotPayment(sessionId: String)
    suspend fun cancelCitySlot(subscriptionId: String, reasonCode: String? = null, note: String? = null)
    suspend fun cancelSubscription(subscriptionId: String) // Alias for cancelCitySlot
    
    // Slot Purchase
    suspend fun purchaseSlot(slot: com.externalpods.rixy.core.model.CitySlot): CitySlotCheckoutResponse
}

class OwnerRepositoryImpl(
    private val ownerApi: OwnerApiService
) : OwnerRepository {

    // Profile
    override suspend fun getProfile(): Owner {
        return try {
            val response = ownerApi.getMe()
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("Profile not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getAnalytics(days: Int): OwnerAnalyticsOverview {
        return try {
            val response = ownerApi.getAnalyticsOverview(days)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("Analytics not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Business
    override suspend fun getBusiness(): Business? {
        return try {
            val response = ownerApi.getBusiness()
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createBusiness(request: CreateBusinessRequest): Business {
        return try {
            val response = ownerApi.createBusiness(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty create business response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updateBusiness(request: UpdateBusinessRequest): Business {
        return try {
            val response = ownerApi.updateBusiness(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty update business response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Listings
    override suspend fun getListings(): List<Listing> {
        return try {
            val response = ownerApi.getListings()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createListing(request: CreateListingRequest): Listing {
        return try {
            val response = ownerApi.createListing(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty create listing response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getListing(listingId: String): Listing {
        return try {
            val response = ownerApi.getListing(listingId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("Listing not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updateListing(listingId: String, request: UpdateListingRequest): Listing {
        return try {
            val response = ownerApi.updateListing(listingId, request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty update listing response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun deleteListing(listingId: String) {
        try {
            val response = ownerApi.deleteListing(listingId)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Favorites
    override suspend fun getFavoriteIds(): List<String> {
        return try {
            val response = ownerApi.getFavoriteListingIds()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun addFavorite(listingId: String) {
        try {
            val response = ownerApi.addFavorite(FavoriteRequest(listingId))
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun removeFavorite(listingId: String) {
        try {
            val response = ownerApi.removeFavorite(listingId)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getFavorites(): List<Listing> {
        return try {
            val response = ownerApi.getFavorites()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Uploads
    override suspend fun presignUpload(filename: String, contentType: String): PresignUploadResponse {
        return try {
            val response = ownerApi.presignUpload(PresignUploadRequest(filename, contentType))
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty presign response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Featured Placements
    override suspend fun getFeaturedPlacements(): List<FeaturedPlacement> {
        return try {
            val response = ownerApi.getFeaturedPlacements()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createFeaturedCheckout(listingId: String): CheckoutResponse {
        return try {
            val response = ownerApi.createFeaturedCheckout(FeaturedCheckoutRequest(listingId))
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty checkout response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun retryFeaturedCheckout(listingId: String): CheckoutResponse {
        return try {
            val response = ownerApi.retryFeaturedCheckout(listingId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty retry checkout response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun cancelFeaturedCheckout(listingId: String) {
        try {
            val response = ownerApi.cancelFeaturedCheckout(listingId)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun renewFeaturedCheckout(listingId: String): CheckoutResponse {
        return try {
            val response = ownerApi.renewFeaturedCheckout(listingId)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty renew checkout response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun confirmFeaturedPayment(listingId: String) {
        try {
            val response = ownerApi.confirmFeaturedPayment(listingId)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    // Business Sections
    override suspend fun getBusinessSections(): List<BusinessSection> {
        return try {
            val response = ownerApi.getBusinessSections()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createBusinessSection(request: CreateBusinessSectionRequest): BusinessSection {
        return try {
            val response = ownerApi.createBusinessSection(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty create section response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun updateBusinessSection(sectionId: String, request: UpdateBusinessSectionRequest): BusinessSection {
        return try {
            val response = ownerApi.updateBusinessSection(sectionId, request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty update section response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun deleteBusinessSection(sectionId: String) {
        try {
            val response = ownerApi.deleteBusinessSection(sectionId)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCitySlotAvailability(citySlug: String): CitySlotAvailabilityResponse {
        return try {
            val response = ownerApi.getCitySlotAvailability(citySlug)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.NotFound("Slot availability not found")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCitySlotSubscriptions(): List<CitySlotSubscription> {
        return try {
            val response = ownerApi.getCitySlotSubscriptions()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun getCitySlotSubscriptionHistory(): List<CitySlotSubscription> {
        return try {
            val response = ownerApi.getCitySlotSubscriptionHistory()
            if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun createCitySlotCheckout(request: CreateCitySlotCheckoutRequest): CitySlotCheckoutResponse {
        return try {
            val response = ownerApi.createCitySlotCheckout(request)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty slot checkout response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun retryCitySlotPayment(
        subscriptionId: String, 
        request: CitySlotActionRequest?
    ): CitySlotCheckoutResponse {
        return try {
            val actionRequest = request ?: CitySlotActionRequest()
            val response = ownerApi.retryCitySlotPayment(subscriptionId, actionRequest)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty retry payment response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun renewCitySlotSubscription(
        subscriptionId: String, 
        request: CitySlotActionRequest?
    ): CitySlotCheckoutResponse {
        return try {
            val actionRequest = request ?: CitySlotActionRequest()
            val response = ownerApi.renewCitySlotSubscription(subscriptionId, actionRequest)
            if (response.isSuccessful) {
                response.body()?.data ?: throw ApiError.DecodingError(message = "Empty renew subscription response")
            } else {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun confirmCitySlotPayment(sessionId: String) {
        try {
            val response = ownerApi.confirmCitySlotPayment(ConfirmPaymentRequest(sessionId))
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun cancelCitySlot(subscriptionId: String, reasonCode: String?, note: String?) {
        try {
            val cancelRequest = CancelSlotRequest(reasonCode ?: "OWNER_CANCELED", note)
            val response = ownerApi.cancelCitySlot(subscriptionId, cancelRequest)
            if (!response.isSuccessful) {
                throw ApiError.fromHttpCode(response.code(), response.errorBody()?.string())
            }
        } catch (e: Exception) {
            throw ApiError.fromThrowable(e)
        }
    }

    override suspend fun cancelSubscription(subscriptionId: String) {
        // Alias for cancelCitySlot with default parameters
        cancelCitySlot(subscriptionId)
    }

    override suspend fun purchaseSlot(slot: CitySlot): CitySlotCheckoutResponse {
        val businessId = getBusiness()?.id
            ?: throw ApiError.NotFound("Business not found for city slot checkout")
        if (slot.listingId.isNullOrBlank()) {
            throw ApiError.ValidationError(
                fieldErrors = mapOf("listingId" to "Listing is required for city slot checkout")
            )
        }
        val request = CreateCitySlotCheckoutRequest(
            businessId = businessId,
            slotType = slot.type,
            slotIndex = slot.slotIndex,
            listingId = slot.listingId
        )
        return createCitySlotCheckout(request)
    }
}

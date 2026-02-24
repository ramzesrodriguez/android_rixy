package com.externalpods.rixy.domain.usecase.business

import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.data.repository.BusinessRepository
import com.externalpods.rixy.data.repository.ListingRepository
import com.externalpods.rixy.service.AnalyticsService

class GetBusinessUseCase(
    private val businessRepository: BusinessRepository,
    private val listingRepository: ListingRepository,
    private val analyticsService: AnalyticsService
) {
    suspend operator fun invoke(citySlug: String, businessId: String): Result<Business> {
        return try {
            val business = businessRepository.getBusinessDetail(citySlug, businessId)
            analyticsService.trackView(citySlug, "BUSINESS", businessId)
            Result.success(business)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

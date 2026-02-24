package com.externalpods.rixy.domain.usecase.listing

import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.PaginatedResponse
import com.externalpods.rixy.data.repository.ListingRepository

class GetListingsUseCase(private val listingRepository: ListingRepository) {
    suspend operator fun invoke(
        citySlug: String,
        type: String? = null,
        category: String? = null,
        search: String? = null,
        cursor: String? = null
    ): Result<PaginatedResponse<Listing>> {
        return try {
            Result.success(listingRepository.getListings(citySlug, type, category, search, cursor))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

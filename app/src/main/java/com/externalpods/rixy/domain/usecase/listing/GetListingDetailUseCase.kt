package com.externalpods.rixy.domain.usecase.listing

import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.data.repository.ListingRepository

class GetListingDetailUseCase(private val listingRepository: ListingRepository) {
    suspend operator fun invoke(citySlug: String, listingId: String): Result<Listing> {
        return try {
            val listing = listingRepository.getListingDetail(citySlug, listingId)
            listingRepository.trackView(citySlug, "LISTING", listingId)
            Result.success(listing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

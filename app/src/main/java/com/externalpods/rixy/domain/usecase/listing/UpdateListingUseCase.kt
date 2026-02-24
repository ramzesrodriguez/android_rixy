package com.externalpods.rixy.domain.usecase.listing

import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.network.dto.UpdateListingRequest
import com.externalpods.rixy.data.repository.OwnerRepository

class UpdateListingUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(listingId: String, request: UpdateListingRequest): Result<Listing> {
        return try {
            Result.success(ownerRepository.updateListing(listingId, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

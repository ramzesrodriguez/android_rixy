package com.externalpods.rixy.domain.usecase.listing

import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.network.dto.CreateListingRequest
import com.externalpods.rixy.data.repository.OwnerRepository

class CreateListingUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(request: CreateListingRequest): Result<Listing> {
        return try {
            Result.success(ownerRepository.createListing(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

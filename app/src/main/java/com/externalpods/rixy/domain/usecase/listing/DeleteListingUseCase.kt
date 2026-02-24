package com.externalpods.rixy.domain.usecase.listing

import com.externalpods.rixy.data.repository.OwnerRepository

class DeleteListingUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(listingId: String): Result<Unit> {
        return try {
            ownerRepository.deleteListing(listingId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

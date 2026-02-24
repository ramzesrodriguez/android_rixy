package com.externalpods.rixy.domain.usecase.owner

import com.externalpods.rixy.core.model.Owner
import com.externalpods.rixy.data.repository.OwnerRepository

class GetOwnerProfileUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(): Result<Owner> {
        return try {
            Result.success(ownerRepository.getProfile())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

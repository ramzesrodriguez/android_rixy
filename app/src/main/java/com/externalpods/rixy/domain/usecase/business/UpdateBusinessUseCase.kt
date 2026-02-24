package com.externalpods.rixy.domain.usecase.business

import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.network.dto.UpdateBusinessRequest
import com.externalpods.rixy.data.repository.OwnerRepository

class UpdateBusinessUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(request: UpdateBusinessRequest): Result<Business> {
        return try {
            Result.success(ownerRepository.updateBusiness(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

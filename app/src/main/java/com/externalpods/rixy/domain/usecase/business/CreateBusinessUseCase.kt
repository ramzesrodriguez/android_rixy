package com.externalpods.rixy.domain.usecase.business

import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.network.dto.CreateBusinessRequest
import com.externalpods.rixy.data.repository.OwnerRepository

class CreateBusinessUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(request: CreateBusinessRequest): Result<Business> {
        return try {
            Result.success(ownerRepository.createBusiness(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

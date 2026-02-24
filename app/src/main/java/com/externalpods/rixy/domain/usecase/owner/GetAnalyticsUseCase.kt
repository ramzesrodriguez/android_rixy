package com.externalpods.rixy.domain.usecase.owner

import com.externalpods.rixy.core.model.OwnerAnalyticsOverview
import com.externalpods.rixy.data.repository.OwnerRepository

class GetAnalyticsUseCase(private val ownerRepository: OwnerRepository) {
    suspend operator fun invoke(days: Int = 30): Result<OwnerAnalyticsOverview> {
        return try {
            Result.success(ownerRepository.getAnalytics(days))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

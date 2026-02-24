package com.externalpods.rixy.domain.usecase.admin

import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.model.ModerationAction
import com.externalpods.rixy.core.network.dto.ModerateRequest
import com.externalpods.rixy.data.repository.AdminRepository

class ModerateBusinessUseCase(private val adminRepository: AdminRepository) {
    suspend operator fun invoke(
        businessId: String,
        action: ModerationAction,
        reasonCode: String? = null,
        note: String? = null
    ): Result<Business> {
        return try {
            val request = ModerateRequest(action, reasonCode, note)
            Result.success(adminRepository.moderateBusiness(businessId, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

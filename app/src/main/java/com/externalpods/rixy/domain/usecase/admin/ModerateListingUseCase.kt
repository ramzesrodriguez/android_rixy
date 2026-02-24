package com.externalpods.rixy.domain.usecase.admin

import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ModerationAction
import com.externalpods.rixy.core.network.dto.ModerateRequest
import com.externalpods.rixy.data.repository.AdminRepository

class ModerateListingUseCase(private val adminRepository: AdminRepository) {
    suspend operator fun invoke(
        listingId: String,
        action: ModerationAction,
        reasonCode: String? = null,
        note: String? = null
    ): Result<Listing> {
        return try {
            val request = ModerateRequest(action, reasonCode, note)
            Result.success(adminRepository.moderateListing(listingId, request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.externalpods.rixy.feature.admin.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingStatus
import com.externalpods.rixy.core.model.ModerationAction
import com.externalpods.rixy.core.network.dto.ModerateRequest
import com.externalpods.rixy.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModerationUiState(
    val listings: List<Listing> = emptyList(),
    val businesses: List<Business> = emptyList(),
    val selectedStatus: String? = "PENDING_REVIEW",
    val isLoading: Boolean = false,
    val isModerating: Boolean = false,
    val error: String? = null,
    val moderationSuccess: Boolean = false
)

class ModerationViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModerationUiState())
    val uiState: StateFlow<ModerationUiState> = _uiState.asStateFlow()

    init {
        loadModerationQueue()
    }

    fun loadModerationQueue() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val listings = adminRepository.getModerationListings(
                    status = _uiState.value.selectedStatus
                )
                val businesses = adminRepository.getModerationBusinesses(
                    status = _uiState.value.selectedStatus
                )
                
                _uiState.update { 
                    it.copy(
                        listings = listings,
                        businesses = businesses,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun onStatusFilterSelected(status: String?) {
        _uiState.update { it.copy(selectedStatus = status) }
        loadModerationQueue()
    }

    fun moderateListing(listingId: String, action: ModerationAction, reason: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isModerating = true, error = null) }
            
            try {
                adminRepository.moderateListing(
                    listingId = listingId,
                    request = ModerateRequest(action, reasonCode = null, note = reason)
                )
                _uiState.update { 
                    it.copy(
                        isModerating = false,
                        moderationSuccess = true
                    )
                }
                loadModerationQueue()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isModerating = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun moderateBusiness(businessId: String, action: ModerationAction, reason: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isModerating = true, error = null) }
            
            try {
                adminRepository.moderateBusiness(
                    businessId = businessId,
                    request = ModerateRequest(action, reasonCode = null, note = reason)
                )
                _uiState.update { 
                    it.copy(
                        isModerating = false,
                        moderationSuccess = true
                    )
                }
                loadModerationQueue()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isModerating = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onModerationSuccessDismissed() {
        _uiState.update { it.copy(moderationSuccess = false) }
    }
}

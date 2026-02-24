package com.externalpods.rixy.feature.owner.featured

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.FeaturedPlacement
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeaturedCampaignsUiState(
    val placements: List<FeaturedPlacement> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatingCheckout: Boolean = false,
    val error: String? = null,
    val checkoutUrl: String? = null
)

class FeaturedCampaignsViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeaturedCampaignsUiState())
    val uiState: StateFlow<FeaturedCampaignsUiState> = _uiState.asStateFlow()

    init {
        loadPlacements()
    }

    fun loadPlacements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val placements = ownerRepository.getFeaturedPlacements()
                _uiState.update { 
                    it.copy(
                        placements = placements,
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

    fun createFeaturedCheckout(listingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCheckout = true, error = null) }
            
            try {
                val checkout = ownerRepository.createFeaturedCheckout(listingId)
                checkout.checkoutUrl?.let { url ->
                    _uiState.update { it.copy(checkoutUrl = url, isCreatingCheckout = false) }
                } ?: run {
                    _uiState.update { it.copy(isCreatingCheckout = false, error = "No checkout URL received") }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCreatingCheckout = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun onCheckoutCompleted() {
        _uiState.update { it.copy(checkoutUrl = null) }
        loadPlacements()
    }

    fun onCheckoutCancelled() {
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

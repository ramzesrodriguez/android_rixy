package com.externalpods.rixy.feature.owner.featured

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.FeaturedPlacement
import com.externalpods.rixy.core.model.FeaturedPlacementStatus
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingStatus
import com.externalpods.rixy.core.model.ListingType
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FeaturedListingActionState {
    CHECKOUT,
    RETRY_CANCEL,
    RENEW,
    ALREADY_FEATURED
}

data class FeaturedCampaignsUiState(
    val placements: List<FeaturedPlacement> = emptyList(),
    val listings: List<Listing> = emptyList(),
    val searchQuery: String = "",
    val selectedType: ListingType? = null,
    val actionLoadingListingIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isCreatingCheckout: Boolean = false,
    val error: String? = null,
    val checkoutUrl: String? = null
) {
    // Computed properties for UI
    val activePlacements: List<FeaturedPlacement> 
        get() = placements.filter { it.status == FeaturedPlacementStatus.ACTIVE }
    val availablePlacements: List<FeaturedPlacement> 
        get() = placements.filter { it.status == FeaturedPlacementStatus.PENDING || it.status == FeaturedPlacementStatus.EXPIRED }
    val filteredListings: List<Listing>
        get() {
            val query = searchQuery.trim().lowercase()
            return listings.filter { listing ->
                val matchesType = selectedType == null || listing.type == selectedType
                val matchesQuery = query.isBlank() ||
                    listing.title.lowercase().contains(query) ||
                    (listing.categoryTag ?: "").lowercase().contains(query) ||
                    listing.type.name.lowercase().contains(query)
                matchesType && matchesQuery
            }
        }
}

class FeaturedCampaignsViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeaturedCampaignsUiState())
    val uiState: StateFlow<FeaturedCampaignsUiState> = _uiState.asStateFlow()

    init {
        loadScreenData()
    }

    fun loadScreenData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val placements = ownerRepository.getFeaturedPlacements()
                val listings = ownerRepository.getListings().filter { it.status == ListingStatus.PUBLISHED }
                _uiState.update { 
                    it.copy(
                        placements = placements,
                        listings = listings,
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTypeSelected(type: ListingType?) {
        _uiState.update { it.copy(selectedType = type) }
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
        loadScreenData()
    }

    fun onCheckoutCancelled() {
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun initiateCheckout(listing: Listing) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCheckout = true, error = null) }
            
            try {
                val checkout = ownerRepository.createFeaturedCheckout(listing.id)
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

    fun retryCheckout(listing: Listing) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoadingListingIds = it.actionLoadingListingIds + listing.id) }
            try {
                val checkout = ownerRepository.retryFeaturedCheckout(listing.id)
                checkout.checkoutUrl?.let { url ->
                    _uiState.update { it.copy(checkoutUrl = url) }
                }
                loadScreenData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(actionLoadingListingIds = it.actionLoadingListingIds - listing.id) }
            }
        }
    }

    fun cancelCheckout(listing: Listing) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoadingListingIds = it.actionLoadingListingIds + listing.id) }
            try {
                ownerRepository.cancelFeaturedCheckout(listing.id)
                loadScreenData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(actionLoadingListingIds = it.actionLoadingListingIds - listing.id) }
            }
        }
    }

    fun renewCheckout(listing: Listing) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoadingListingIds = it.actionLoadingListingIds + listing.id) }
            try {
                val checkout = ownerRepository.renewFeaturedCheckout(listing.id)
                checkout.checkoutUrl?.let { url ->
                    _uiState.update { it.copy(checkoutUrl = url) }
                }
                loadScreenData()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(actionLoadingListingIds = it.actionLoadingListingIds - listing.id) }
            }
        }
    }

    fun actionStateFor(listing: Listing): FeaturedListingActionState {
        if (hasPendingPaymentTransaction(listing)) return FeaturedListingActionState.RETRY_CANCEL
        val placement = _uiState.value.placements
            .filter { it.listingId == listing.id }
            .maxByOrNull { it.createdAt }
            ?: return FeaturedListingActionState.CHECKOUT
        if (hasPendingPaymentTransaction(placement)) return FeaturedListingActionState.RETRY_CANCEL
        return when (placement.status) {
            FeaturedPlacementStatus.ACTIVE,
            FeaturedPlacementStatus.PENDING -> FeaturedListingActionState.ALREADY_FEATURED
            FeaturedPlacementStatus.EXPIRED,
            FeaturedPlacementStatus.CANCELED -> FeaturedListingActionState.RENEW
        }
    }

    private fun hasPendingPaymentTransaction(listing: Listing): Boolean {
        return listing.paymentTransactions?.any { tx ->
            tx.status?.name == "PENDING" || !tx.checkoutSessionId.isNullOrBlank()
        } == true
    }

    private fun hasPendingPaymentTransaction(placement: FeaturedPlacement): Boolean {
        return placement.status == FeaturedPlacementStatus.PENDING ||
            placement.paymentTransactions?.any { tx ->
                tx.status?.name == "PENDING" || !tx.checkoutSessionId.isNullOrBlank()
            } == true
    }
}

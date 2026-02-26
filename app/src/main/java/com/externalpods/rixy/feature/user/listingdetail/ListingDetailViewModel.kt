package com.externalpods.rixy.feature.user.listingdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.domain.usecase.listing.GetListingDetailUseCase
import com.externalpods.rixy.service.AnalyticsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListingDetailUiState(
    val listing: Listing? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showContactOptions: Boolean = false,
    val isFavorite: Boolean = false
)

class ListingDetailViewModel(
    private val getListingDetailUseCase: GetListingDetailUseCase,
    private val analyticsService: AnalyticsService,
    private val citySlug: String,
    private val listingId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingDetailUiState())
    val uiState: StateFlow<ListingDetailUiState> = _uiState.asStateFlow()

    init {
        if (citySlug.isNotEmpty() && listingId.isNotEmpty()) {
            loadListing()
        } else {
            _uiState.update {
                it.copy(error = "No se pudo abrir el detalle (citySlug/listingId faltante)")
            }
        }
    }

    fun loadListing() {
        if (citySlug.isEmpty() || listingId.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getListingDetailUseCase(citySlug, listingId)
                .onSuccess { listing ->
                    _uiState.update { 
                        it.copy(
                            listing = listing,
                            isLoading = false
                        )
                    }
                    // Track view
                    analyticsService.trackView(citySlug, "LISTING", listingId)
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun trackView() {
        if (citySlug.isNotEmpty() && listingId.isNotEmpty()) {
            viewModelScope.launch {
                analyticsService.trackView(citySlug, "LISTING", listingId)
            }
        }
    }

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
        // TODO: Persist favorite state to repository
    }

    fun onContactClick() {
        _uiState.update { it.copy(showContactOptions = true) }
    }

    fun dismissContactOptions() {
        _uiState.update { it.copy(showContactOptions = false) }
    }

    fun onWhatsAppClick(phone: String) {
        // Open WhatsApp - handled by UI
    }

    fun onWhatsAppClick() {
        // No-arg variant - handled by UI
    }

    fun onPhoneClick(phone: String) {
        // Open dialer - handled by UI
    }

    fun onWebsiteClick(url: String) {
        // Open browser - handled by UI
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

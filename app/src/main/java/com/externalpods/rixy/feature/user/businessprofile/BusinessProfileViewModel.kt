package com.externalpods.rixy.feature.user.businessprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.data.repository.ListingRepository
import com.externalpods.rixy.domain.usecase.business.GetBusinessUseCase
import com.externalpods.rixy.service.AnalyticsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BusinessProfileUiState(
    val business: Business? = null,
    val listings: List<Listing> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingListings: Boolean = false,
    val error: String? = null
)

class BusinessProfileViewModel(
    private val getBusinessUseCase: GetBusinessUseCase,
    private val listingRepository: ListingRepository,
    private val analyticsService: AnalyticsService,
    private val citySlug: String,
    private val businessId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessProfileUiState())
    val uiState: StateFlow<BusinessProfileUiState> = _uiState.asStateFlow()

    init {
        if (citySlug.isNotEmpty() && businessId.isNotEmpty()) {
            loadBusiness()
        } else {
            _uiState.update {
                it.copy(error = "No se pudo abrir el negocio (citySlug/businessId faltante)")
            }
        }
    }

    fun loadBusiness() {
        if (citySlug.isEmpty() || businessId.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getBusinessUseCase(citySlug, businessId)
                .onSuccess { business ->
                    _uiState.update { 
                        it.copy(
                            business = business,
                            isLoading = false
                        )
                    }
                    // Track view
                    analyticsService.trackView(citySlug, "BUSINESS", businessId)
                    // Load listings
                    loadBusinessListings()
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

    private fun loadBusinessListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingListings = true) }
            
            try {
                val listings = listingRepository.getBusinessListings(citySlug, businessId)
                _uiState.update { it.copy(listings = listings, isLoadingListings = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingListings = false) }
            }
        }
    }

    fun onWhatsAppClick() {
        // Open WhatsApp - handled by UI layer
    }

    fun onPhoneClick() {
        // Open dialer - handled by UI layer
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

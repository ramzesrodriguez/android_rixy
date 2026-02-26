package com.externalpods.rixy.feature.owner.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingStatus
import com.externalpods.rixy.core.model.OwnerAnalyticsOverview
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.domain.usecase.owner.GetAnalyticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OwnerDashboardUiState(
    val analytics: OwnerAnalyticsOverview? = null,
    val business: Business? = null,
    val listingsCount: Int = 0,
    val ownerName: String? = null,
    val publishedCount: Int = 0,
    val draftCount: Int = 0,
    val featuredCount: Int = 0,
    val recentListings: List<Listing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class OwnerDashboardViewModel(
    private val getAnalyticsUseCase: GetAnalyticsUseCase,
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerDashboardUiState())
    val uiState: StateFlow<OwnerDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load analytics
                getAnalyticsUseCase(days = 30)
                    .onSuccess { analytics ->
                        _uiState.update { it.copy(analytics = analytics) }
                    }
                
                // Load business
                val business = ownerRepository.getBusiness()
                _uiState.update { it.copy(business = business) }
                
                // Load listings and calculate counts
                val listings = ownerRepository.getListings()
                val publishedCount = listings.count { it.status == ListingStatus.PUBLISHED }
                val draftCount = listings.count { it.status == ListingStatus.DRAFT }
                val featuredCount = listings.count { listing ->
                    listing.isFeatured == true || (listing.paymentTransactions?.isNotEmpty() == true)
                }
                
                _uiState.update { 
                    it.copy(
                        listingsCount = listings.size,
                        ownerName = business?.name ?: business?.ownerName,
                        publishedCount = publishedCount,
                        draftCount = draftCount,
                        featuredCount = featuredCount,
                        recentListings = listings.take(5),
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

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            try {
                getAnalyticsUseCase(days = 30)
                    .onSuccess { analytics ->
                        _uiState.update { it.copy(analytics = analytics) }
                    }
                
                val listings = ownerRepository.getListings()
                val business = _uiState.value.business
                val publishedCount = listings.count { it.status == ListingStatus.PUBLISHED }
                val draftCount = listings.count { it.status == ListingStatus.DRAFT }
                val featuredCount = listings.count { listing ->
                    listing.isFeatured == true || (listing.paymentTransactions?.isNotEmpty() == true)
                }
                
                _uiState.update { 
                    it.copy(
                        listingsCount = listings.size,
                        ownerName = business?.name ?: business?.ownerName,
                        publishedCount = publishedCount,
                        draftCount = draftCount,
                        featuredCount = featuredCount,
                        recentListings = listings.take(5),
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

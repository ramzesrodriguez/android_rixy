package com.externalpods.rixy.feature.user.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import com.externalpods.rixy.domain.usecase.listing.GetListingsUseCase
import com.externalpods.rixy.navigation.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrowseListingsUiState(
    val listings: List<Listing> = emptyList(),
    val searchQuery: String = "",
    val selectedType: ListingType? = null,
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val nextCursor: String? = null,
    val error: String? = null
)

class BrowseListingsViewModel(
    private val getListingsUseCase: GetListingsUseCase,
    private val appState: AppState
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseListingsUiState())
    val uiState: StateFlow<BrowseListingsUiState> = _uiState.asStateFlow()

    // Expose individual state properties for direct access
    val searchQuery: String get() = _uiState.value.searchQuery
    val selectedType: ListingType? get() = _uiState.value.selectedType
    val hasMorePages: Boolean get() = _uiState.value.hasMorePages

    init {
        loadListings()
    }

    fun loadListings(refresh: Boolean = true) {
        val citySlug = appState.selectedCity.value?.slug ?: return
        
        viewModelScope.launch {
            if (refresh) {
                _uiState.update { it.copy(isLoading = true, error = null, listings = emptyList()) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }
            
            getListingsUseCase(
                citySlug = citySlug,
                type = _uiState.value.selectedType?.name,
                category = _uiState.value.selectedCategory,
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                cursor = if (refresh) null else _uiState.value.nextCursor
            )
                .onSuccess { newListings ->
                    _uiState.update { state ->
                        state.copy(
                            listings = if (refresh) newListings else state.listings + newListings,
                            isLoading = false,
                            isLoadingMore = false,
                            hasMorePages = newListings.size >= 20, // Assuming page size
                            nextCursor = newListings.lastOrNull()?.id
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search() {
        loadListings(refresh = true)
    }

    fun applySearch() {
        loadListings(refresh = true)
    }

    fun onTypeFilterSelected(type: ListingType?) {
        _uiState.update { it.copy(selectedType = type) }
        loadListings(refresh = true)
    }

    fun onTypeSelected(type: ListingType?) {
        onTypeFilterSelected(type)
    }

    fun onCategoryFilterSelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadListings(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.hasMorePages && !_uiState.value.isLoadingMore) {
            loadListings(refresh = false)
        }
    }

    fun loadNextPage() {
        loadMore()
    }

    fun refresh() {
        loadListings(refresh = true)
    }

    fun clearFilters() {
        _uiState.update { 
            it.copy(
                searchQuery = "",
                selectedType = null,
                selectedCategory = null
            )
        }
        loadListings(refresh = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

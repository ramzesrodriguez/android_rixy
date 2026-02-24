package com.externalpods.rixy.feature.user.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import com.externalpods.rixy.domain.usecase.listing.GetListingsUseCase
import com.externalpods.rixy.navigation.AppStateViewModel
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
    private val appState: AppStateViewModel,
    private val citySlugParam: String? = null
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
        val citySlug = resolveCitySlug() ?: return
        
        viewModelScope.launch {
            if (refresh) {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        isLoadingMore = false,
                        error = null,
                        listings = emptyList(),
                        nextCursor = null,
                        hasMorePages = true
                    )
                }
            } else {
                if (_uiState.value.nextCursor.isNullOrBlank()) {
                    _uiState.update { it.copy(hasMorePages = false, isLoadingMore = false) }
                    return@launch
                }
                _uiState.update { it.copy(isLoadingMore = true) }
            }
            
            getListingsUseCase(
                citySlug = citySlug,
                type = _uiState.value.selectedType?.name,
                category = _uiState.value.selectedCategory,
                search = _uiState.value.searchQuery.takeIf { it.isNotBlank() },
                cursor = if (refresh) null else _uiState.value.nextCursor
            )
                .onSuccess { result ->
                    _uiState.update { state ->
                        val combined = if (refresh) {
                            result.data
                        } else {
                            (state.listings + result.data).distinctBy { it.id }
                        }

                        state.copy(
                            listings = combined,
                            nextCursor = result.nextCursor,
                            hasMorePages = !result.nextCursor.isNullOrBlank(),
                            isLoading = false,
                            isLoadingMore = false,
                            error = null
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

    fun loadNextPage() {
        if (!_uiState.value.isLoading && !_uiState.value.isLoadingMore && _uiState.value.hasMorePages) {
            loadListings(refresh = false)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun search() {
        loadListings(refresh = true)
    }

    fun onTypeSelected(type: ListingType?) {
        _uiState.update { it.copy(selectedType = type) }
        loadListings(refresh = true)
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadListings(refresh = true)
    }

    fun refresh() {
        loadListings(refresh = true)
    }

    fun clearFilters() {
        _uiState.update { 
            it.copy(
                selectedType = null,
                selectedCategory = null,
                searchQuery = ""
            )
        }
        loadListings(refresh = true)
    }

    private fun resolveCitySlug(): String? {
        val explicit = citySlugParam?.trim().orEmpty()
        if (explicit.isNotBlank()) return explicit
        return appState.selectedCity.value?.slug
    }
}

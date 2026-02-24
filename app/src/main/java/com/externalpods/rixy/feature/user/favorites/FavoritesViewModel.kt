package com.externalpods.rixy.feature.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<Listing> = emptyList(),
    val filteredFavorites: List<Listing> = emptyList(),
    val searchQuery: String = "",
    val selectedType: ListingType? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class FavoritesViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val favorites = ownerRepository.getFavorites()
                _uiState.update { 
                    it.copy(
                        favorites = favorites,
                        filteredFavorites = favorites,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar favoritos"
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        filterFavorites(query, _uiState.value.selectedType)
    }

    fun onTypeSelected(type: ListingType?) {
        filterFavorites(_uiState.value.searchQuery, type)
    }

    private fun filterFavorites(query: String, type: ListingType?) {
        val filtered = _uiState.value.favorites.filter { listing ->
            val matchesQuery = query.isBlank() || 
                listing.title.contains(query, ignoreCase = true) ||
                listing.description?.contains(query, ignoreCase = true) == true
            
            val matchesType = type == null || listing.type == type
            
            matchesQuery && matchesType
        }
        
        _uiState.update { 
            it.copy(
                searchQuery = query,
                selectedType = type,
                filteredFavorites = filtered
            )
        }
    }

    fun removeFromFavorites(listingId: String) {
        viewModelScope.launch {
            try {
                ownerRepository.removeFavorite(listingId)
                // Remove from local list
                val updated = _uiState.value.favorites.filter { it.id != listingId }
                _uiState.update { 
                    it.copy(
                        favorites = updated,
                        filteredFavorites = updated.filter { f ->
                            val matchesQuery = it.searchQuery.isBlank() || 
                                f.title.contains(it.searchQuery, ignoreCase = true)
                            val matchesType = it.selectedType == null || f.type == it.selectedType
                            matchesQuery && matchesType
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

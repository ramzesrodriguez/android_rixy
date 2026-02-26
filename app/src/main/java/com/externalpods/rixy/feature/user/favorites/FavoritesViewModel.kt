package com.externalpods.rixy.feature.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import com.externalpods.rixy.data.repository.FavoritesRepository
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val ownerRepository: OwnerRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    data class UiState(
        val favorites: List<Listing> = emptyList(),
        val favoriteIds: Set<String> = emptySet(),
        val loadingFavoriteIds: Set<String> = emptySet(),
        val searchQuery: String = "",
        val selectedType: ListingType? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        val filteredFavorites: List<Listing>
            get() {
                val query = searchQuery.trim().lowercase()
                return favorites.filter { listing ->
                    if (selectedType != null && listing.type != selectedType) {
                        return@filter false
                    }
                    if (query.isBlank()) return@filter true
                    listing.title.lowercase().contains(query) ||
                        listing.type.name.lowercase().contains(query) ||
                        (listing.categoryTag ?: "").lowercase().contains(query) ||
                        (listing.business?.name ?: "").lowercase().contains(query)
                }
            }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val favoriteIds = ownerRepository.getFavoriteIds().toSet()
                val favorites = ownerRepository.getListings()
                    .filter { favoriteIds.contains(it.id) }
                    .sortedByDescending { it.updatedAt ?: it.createdAt.orEmpty() }
                favoritesRepository.setFavorites(favorites)

                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        favoriteIds = favoriteIds,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (error: Exception) {
                if (error is ApiError.Unauthorized) {
                    val localFavorites = favoritesRepository.getFavorites().first()
                    _uiState.update {
                        it.copy(
                            favorites = localFavorites,
                            favoriteIds = localFavorites.map { listing -> listing.id }.toSet(),
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Error al cargar favoritos"
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

    fun clearFilters() {
        _uiState.update { it.copy(selectedType = null, searchQuery = "") }
    }

    fun toggleFavorite(listingId: String) {
        val state = _uiState.value
        if (state.loadingFavoriteIds.contains(listingId)) return
        if (!state.favoriteIds.contains(listingId)) return

        val previousFavorites = state.favorites
        val previousFavoriteIds = state.favoriteIds

        _uiState.update {
            it.copy(
                loadingFavoriteIds = it.loadingFavoriteIds + listingId,
                favoriteIds = it.favoriteIds - listingId,
                favorites = it.favorites.filterNot { listing -> listing.id == listingId }
            )
        }

        viewModelScope.launch {
            runCatching {
                ownerRepository.removeFavorite(listingId)
                favoritesRepository.removeFavorite(listingId)
            }.onFailure { error ->
                if (error is ApiError.Unauthorized) {
                    favoritesRepository.removeFavorite(listingId)
                    return@onFailure
                }
                _uiState.update {
                    it.copy(
                        favorites = previousFavorites,
                        favoriteIds = previousFavoriteIds,
                        error = error.message ?: "No se pudo actualizar favorito"
                    )
                }
            }

            _uiState.update {
                it.copy(loadingFavoriteIds = it.loadingFavoriteIds - listingId)
            }
        }
    }
}

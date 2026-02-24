package com.externalpods.rixy.feature.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _favorites = MutableStateFlow<List<Listing>>(emptyList())
    val favorites: StateFlow<List<Listing>> = _favorites.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                favoritesRepository.getFavorites().collect {
                    _favorites.value = it
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun removeFavorite(listingId: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(listingId)
        }
    }
}

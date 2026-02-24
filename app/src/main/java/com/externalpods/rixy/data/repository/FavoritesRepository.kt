package com.externalpods.rixy.data.repository

import com.externalpods.rixy.core.model.Listing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepository {
    
    private val _favorites = MutableStateFlow<List<Listing>>(emptyList())
    
    fun getFavorites(): Flow<List<Listing>> = _favorites.asStateFlow()
    
    suspend fun addFavorite(listing: Listing) {
        val current = _favorites.value.toMutableList()
        if (!current.any { it.id == listing.id }) {
            current.add(listing)
            _favorites.value = current
        }
    }
    
    suspend fun removeFavorite(listingId: String) {
        _favorites.value = _favorites.value.filter { it.id != listingId }
    }
    
    suspend fun isFavorite(listingId: String): Boolean {
        return _favorites.value.any { it.id == listingId }
    }
}

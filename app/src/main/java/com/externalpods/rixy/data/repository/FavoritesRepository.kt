package com.externalpods.rixy.data.repository

import com.externalpods.rixy.data.local.DataStoreManager
import com.externalpods.rixy.core.model.Listing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class FavoritesRepository(
    private val dataStoreManager: DataStoreManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }
    private val _favorites = MutableStateFlow<List<Listing>>(emptyList())

    init {
        scope.launch {
            dataStoreManager.favoritesJson.collectLatest { raw ->
                val decoded = decode(raw)
                _favorites.value = decoded
            }
        }
    }

    fun getFavorites(): Flow<List<Listing>> = _favorites.asStateFlow()

    suspend fun addFavorite(listing: Listing) {
        val current = _favorites.value.toMutableList()
        if (!current.any { it.id == listing.id }) {
            current.add(listing)
            _favorites.value = current
            persist(current)
        }
    }

    suspend fun removeFavorite(listingId: String) {
        val updated = _favorites.value.filter { it.id != listingId }
        _favorites.value = updated
        persist(updated)
    }

    suspend fun setFavorites(listings: List<Listing>) {
        _favorites.value = listings
        persist(listings)
    }

    suspend fun isFavorite(listingId: String): Boolean {
        return _favorites.value.any { it.id == listingId }
    }

    suspend fun syncLocalToRemote(ownerRepository: OwnerRepository) {
        val local = _favorites.value
        if (local.isEmpty()) return

        val localIds = local.map { it.id }.toSet()
        val remoteIds = ownerRepository.getFavoriteIds().toSet()

        localIds
            .filterNot { remoteIds.contains(it) }
            .forEach { listingId ->
                runCatching { ownerRepository.addFavorite(listingId) }
            }

        val syncedIds = ownerRepository.getFavoriteIds().toSet()
        val syncedListings = ownerRepository.getListings()
            .filter { syncedIds.contains(it.id) }
            .sortedByDescending { it.updatedAt ?: it.createdAt.orEmpty() }

        setFavorites(syncedListings)
    }

    private suspend fun persist(listings: List<Listing>) {
        if (listings.isEmpty()) {
            dataStoreManager.clearFavorites()
            return
        }
        val raw = json.encodeToString(ListSerializer(Listing.serializer()), listings)
        dataStoreManager.saveFavoritesJson(raw)
    }

    private fun decode(raw: String?): List<Listing> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(Listing.serializer()), raw)
        }.getOrElse { emptyList() }
    }
}

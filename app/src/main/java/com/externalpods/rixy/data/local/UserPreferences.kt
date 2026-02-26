package com.externalpods.rixy.data.local

import kotlinx.coroutines.flow.first

/**
 * UserPreferences - Synchronous wrapper for DataStoreManager
 * Mirrors iOS UserDefaultsManager for easier state restoration
 */
class UserPreferences(private val dataStoreManager: DataStoreManager) {
    
    suspend fun isAuthenticated(): Boolean = dataStoreManager.isAuthenticated.first()
    
    suspend fun getCurrentUserId(): String? = dataStoreManager.currentUserId.first()
    
    suspend fun getCurrentUserEmail(): String? = dataStoreManager.currentUserEmail.first()
    
    suspend fun setAuthenticated(value: Boolean) {
        // This is set via saveAuthState
    }
    
    suspend fun setCurrentUser(userId: String, email: String) {
        dataStoreManager.saveAuthState(userId, email)
    }
    
    suspend fun clearAuth() {
        dataStoreManager.clearAuthState()
    }
    
    suspend fun getSelectedCityId(): String? = dataStoreManager.selectedCityId.first()
    
    suspend fun getSelectedCityName(): String? = dataStoreManager.selectedCityName.first()
    
    suspend fun getSelectedCitySlug(): String? = dataStoreManager.selectedCitySlug.first()
    
    suspend fun getSelectedCityState(): String? = dataStoreManager.selectedCityState.first()
    
    suspend fun getSelectedCityCountry(): String? = dataStoreManager.selectedCityCountry.first()
    
    suspend fun setSelectedCity(id: String, name: String, slug: String, state: String?, country: String?) {
        dataStoreManager.saveSelectedCity(id, slug, name, state, country)
    }
    
    suspend fun clearCity() {
        dataStoreManager.clearSelectedCity()
    }
    
    suspend fun getAppMode(): com.externalpods.rixy.core.model.AppMode = dataStoreManager.currentMode.first()
    
    suspend fun setAppMode(mode: com.externalpods.rixy.core.model.AppMode) {
        dataStoreManager.saveMode(mode)
    }
}

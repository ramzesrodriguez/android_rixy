package com.externalpods.rixy.navigation

import com.externalpods.rixy.core.model.AppMode
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.Owner
import com.externalpods.rixy.data.local.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Global application state following UDF pattern.
 * Equivalent to iOS AppState (@Observable @MainActor)
 */
class AppState(
    private val dataStoreManager: DataStoreManager
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // Individual state flows
    private val _currentMode = MutableStateFlow(AppMode.USER)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()
    
    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _currentUser = MutableStateFlow<Owner?>(null)
    val currentUser: StateFlow<Owner?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        scope.launch {
            loadPersistedState()
        }
    }
    
    private suspend fun loadPersistedState() {
        _isLoading.value = true
        try {
            // Load saved mode
            val savedMode = dataStoreManager.currentMode.first()
            _currentMode.value = savedMode
            
            // Load saved city
            val cityId = dataStoreManager.selectedCityId.first()
            val citySlug = dataStoreManager.selectedCitySlug.first()
            val cityName = dataStoreManager.selectedCityName.first()
            
            if (cityId != null && citySlug != null) {
                _selectedCity.value = City(
                    id = cityId,
                    name = cityName ?: "",
                    slug = citySlug
                )
            }
            
            // Load auth state
            val isAuth = dataStoreManager.isAuthenticated.first()
            val userId = dataStoreManager.currentUserId.first()
            val userEmail = dataStoreManager.currentUserEmail.first()
            
            _isAuthenticated.value = isAuth && userId != null
        } finally {
            _isLoading.value = false
        }
    }
    
    // Actions
    fun switchMode(mode: AppMode) {
        _currentMode.value = mode
        scope.launch {
            dataStoreManager.saveMode(mode)
        }
    }
    
    fun selectCity(city: City) {
        _selectedCity.value = city
        scope.launch {
            dataStoreManager.saveSelectedCity(city.id, city.slug, city.name)
        }
    }
    
    fun clearSelectedCity() {
        _selectedCity.value = null
        scope.launch {
            dataStoreManager.clearSelectedCity()
        }
    }
    
    fun signIn(user: Owner) {
        _currentUser.value = user
        _isAuthenticated.value = true
        scope.launch {
            dataStoreManager.saveAuthState(user.id, user.email)
        }
    }
    
    fun signOut() {
        _currentUser.value = null
        _isAuthenticated.value = false
        _currentMode.value = AppMode.USER
        scope.launch {
            dataStoreManager.clearAuthState()
            dataStoreManager.saveMode(AppMode.USER)
        }
    }
    
    fun updateUser(user: Owner) {
        _currentUser.value = user
    }
    
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}

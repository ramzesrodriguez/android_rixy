package com.externalpods.rixy.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.Owner
import com.externalpods.rixy.core.model.OwnerRole
import com.externalpods.rixy.core.model.OwnerStatus
import com.externalpods.rixy.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AppState - Central state management (mirrors iOS AppState)
 * 
 * Manages:
 * - Current app mode (user/owner/admin)
 * - Selected city
 * - Authentication state
 * - Current user
 */
enum class AppMode {
    USER,
    OWNER,
    ADMIN
}

class AppStateViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _currentMode = mutableStateOf(AppMode.USER)
    val currentMode: State<AppMode> = _currentMode
    
    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()
    
    private val _isAuthenticated = mutableStateOf(false)
    val isAuthenticated: State<Boolean> = _isAuthenticated
    
    private val _currentUser = MutableStateFlow<Owner?>(null)
    val currentUser: StateFlow<Owner?> = _currentUser.asStateFlow()
    
    init {
        restoreState()
    }
    
    private fun restoreState() {
        viewModelScope.launch {
            // Restore auth state
            _isAuthenticated.value = userPreferences.isAuthenticated()
            
            // Restore user
            val userId = userPreferences.getCurrentUserId()
            val userEmail = userPreferences.getCurrentUserEmail()
            if (userId != null && userEmail != null) {
                _currentUser.value = Owner(
                    id = userId,
                    email = userEmail,
                    role = OwnerRole.OWNER,
                    status = OwnerStatus.ACTIVE
                )
            }
            
            // Restore city
            val cityId = userPreferences.getSelectedCityId()
            val cityName = userPreferences.getSelectedCityName()
            val citySlug = userPreferences.getSelectedCitySlug()
            if (cityId != null && cityName != null && citySlug != null) {
                _selectedCity.value = City(
                    id = cityId,
                    name = cityName,
                    slug = citySlug,
                    isActive = true,
                    isPublishingEnabled = true,
                    isAdsEnabled = false
                )
            }
        }
    }
    
    fun switchMode(mode: AppMode) {
        _currentMode.value = mode
    }
    
    fun selectCity(city: City) {
        _selectedCity.value = city
        viewModelScope.launch {
            userPreferences.setSelectedCity(city.id, city.name, city.slug, city.state, city.country)
        }
    }
    
    fun signIn(user: Owner) {
        _currentUser.value = user
        _isAuthenticated.value = true
        viewModelScope.launch {
            userPreferences.setAuthenticated(true)
            userPreferences.setCurrentUser(user.id, user.email)
        }
    }
    
    fun signOut() {
        _currentUser.value = null
        _isAuthenticated.value = false
        _selectedCity.value = null
        viewModelScope.launch {
            userPreferences.clearAuth()
            userPreferences.clearCity()
        }
    }
}

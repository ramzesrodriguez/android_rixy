package com.externalpods.rixy.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.AppMode
import com.externalpods.rixy.navigation.AppMode as NavigationAppMode
import com.externalpods.rixy.navigation.AppStateViewModel
import com.externalpods.rixy.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userEmail: String = "",
    val currentMode: AppMode = AppMode.USER,
    val isSigningOut: Boolean = false,
    val signOutSuccess: Boolean = false
)

class SettingsViewModel(
    private val authService: AuthService,
    private val appState: AppStateViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Map navigation AppMode to core AppMode
            val mode = when (appState.currentMode.value) {
                NavigationAppMode.USER -> AppMode.USER
                NavigationAppMode.OWNER -> AppMode.OWNER
                NavigationAppMode.ADMIN -> AppMode.ADMIN
            }
            _uiState.update { it.copy(currentMode = mode) }
        }
        viewModelScope.launch {
            appState.currentUser.collect { user ->
                _uiState.update { it.copy(userEmail = user?.email ?: "") }
            }
        }
    }

    fun switchMode(mode: AppMode) {
        val navMode = when (mode) {
            AppMode.USER -> NavigationAppMode.USER
            AppMode.OWNER -> NavigationAppMode.OWNER
            AppMode.ADMIN -> NavigationAppMode.ADMIN
        }
        appState.switchMode(navMode)
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSigningOut = true) }
            
            authService.signOut()
            appState.signOut()
            
            _uiState.update { it.copy(isSigningOut = false, signOutSuccess = true) }
        }
    }
}

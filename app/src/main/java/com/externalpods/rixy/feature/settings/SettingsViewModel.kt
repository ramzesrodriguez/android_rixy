package com.externalpods.rixy.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.AppMode
import com.externalpods.rixy.navigation.AppState
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
    private val appState: AppState
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Observe app state changes
            appState.currentMode.collect { mode ->
                _uiState.update { it.copy(currentMode = mode) }
            }
        }
        viewModelScope.launch {
            appState.currentUser.collect { user ->
                _uiState.update { it.copy(userEmail = user?.email ?: "") }
            }
        }
    }

    fun switchMode(mode: AppMode) {
        appState.switchMode(mode)
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

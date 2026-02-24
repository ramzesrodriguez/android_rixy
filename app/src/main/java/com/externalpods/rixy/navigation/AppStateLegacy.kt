package com.externalpods.rixy.navigation

import androidx.compose.runtime.State
import com.externalpods.rixy.core.model.City
import kotlinx.coroutines.flow.StateFlow
import org.koin.java.KoinJavaComponent.inject

/**
 * AppStateLegacy - Compatibility bridge for old code referencing AppState
 * 
 * This class provides backwards compatibility while transitioning to AppStateViewModel.
 * It delegates to AppStateViewModel via Koin.
 */
class AppState private constructor() {
    
    private val viewModel: AppStateViewModel by inject(AppStateViewModel::class.java)
    
    val currentMode: State<AppMode> get() = viewModel.currentMode
    val isAuthenticated: State<Boolean> get() = viewModel.isAuthenticated
    val selectedCity: StateFlow<City?> get() = viewModel.selectedCity
    
    fun switchMode(mode: AppMode) = viewModel.switchMode(mode)
    fun selectCity(city: City) = viewModel.selectCity(city)
    fun signOut() = viewModel.signOut()
    
    companion object {
        @Volatile
        private var instance: AppState? = null
        
        fun getInstance(): AppState {
            return instance ?: synchronized(this) {
                instance ?: AppState().also { instance = it }
            }
        }
    }
}

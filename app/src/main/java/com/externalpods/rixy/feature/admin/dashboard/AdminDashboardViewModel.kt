package com.externalpods.rixy.feature.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val pendingListingsCount: Int = 0,
    val pendingBusinessesCount: Int = 0,
    val totalUsers: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminDashboardViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val listingsCount = adminRepository.getPendingListingsCount()
                val businessesCount = adminRepository.getPendingBusinessesCount()
                val users = adminRepository.getUsers()
                
                _uiState.update { 
                    it.copy(
                        pendingListingsCount = listingsCount.values.sum(),
                        pendingBusinessesCount = businessesCount.values.sum(),
                        totalUsers = users.size,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

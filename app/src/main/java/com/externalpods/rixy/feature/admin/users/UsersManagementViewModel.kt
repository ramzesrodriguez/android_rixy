package com.externalpods.rixy.feature.admin.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Owner
import com.externalpods.rixy.core.model.OwnerRole
import com.externalpods.rixy.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsersManagementUiState(
    val users: List<Owner> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UsersManagementViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsersManagementUiState())
    val uiState: StateFlow<UsersManagementUiState> = _uiState.asStateFlow()
    
    init { loadUsers() }
    
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val users = adminRepository.getUsers()
                _uiState.update { it.copy(users = users, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun updateUserRole(userId: String, role: OwnerRole) {
        viewModelScope.launch {
            try {
                adminRepository.updateUserRole(userId, role)
                loadUsers()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun suspendUser(userId: String) {
        viewModelScope.launch {
            try {
                adminRepository.suspendUser(userId)
                loadUsers()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

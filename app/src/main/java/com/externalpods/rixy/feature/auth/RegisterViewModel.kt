package com.externalpods.rixy.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, error = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null, error = null) }
    }

    fun register() {
        val state = _uiState.value
        
        // Validation
        when {
            state.email.isBlank() -> {
                _uiState.update { it.copy(error = "Por favor ingresa tu email") }
                return
            }
            state.password.isBlank() -> {
                _uiState.update { it.copy(error = "Por favor ingresa una contraseña") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
            state.password != state.confirmPassword -> {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authService.signUp(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al crear cuenta"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

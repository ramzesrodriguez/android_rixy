package com.externalpods.rixy.feature.admin.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CitiesManagementUiState(
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CitiesManagementViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CitiesManagementUiState())
    val uiState: StateFlow<CitiesManagementUiState> = _uiState.asStateFlow()
    
    init { loadCities() }
    
    fun loadCities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val cities = adminRepository.getCities()
                _uiState.update { it.copy(cities = cities, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun createCity(name: String, slug: String) {
        viewModelScope.launch {
            try {
                adminRepository.createCity(name, slug)
                loadCities()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun updateCity(id: String, name: String, slug: String) {
        viewModelScope.launch {
            try {
                adminRepository.updateCity(id, name, slug)
                loadCities()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun toggleCityActive(id: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                adminRepository.updateCityStatus(id, isActive)
                loadCities()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

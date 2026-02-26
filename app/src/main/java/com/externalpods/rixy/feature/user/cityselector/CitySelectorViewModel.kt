package com.externalpods.rixy.feature.user.cityselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.domain.usecase.city.GetCitiesUseCase
import com.externalpods.rixy.navigation.AppStateViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CitySelectorUiState(
    val cities: List<City> = emptyList(),
    val filteredCities: List<City> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class CitySelectorViewModel(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val appState: AppStateViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySelectorUiState())
    val uiState: StateFlow<CitySelectorUiState> = _uiState.asStateFlow()
    
    private var allCities: List<City> = emptyList()

    init {
        loadCities()
    }

    fun loadCities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getCitiesUseCase()
                .onSuccess { cities ->
                    allCities = cities
                    _uiState.update { 
                        it.copy(
                            cities = cities,
                            filteredCities = cities,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load cities"
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        val filtered = if (query.isBlank()) {
            allCities
        } else {
            allCities.filter { city ->
                city.name.contains(query, ignoreCase = true) ||
                city.state?.contains(query, ignoreCase = true) == true ||
                (city.country?.contains(query, ignoreCase = true) == true)
            }
        }
        
        _uiState.update { 
            it.copy(
                searchQuery = query,
                filteredCities = filtered
            )
        }
    }

    fun selectCity(city: City) {
        appState.selectCity(city)
    }

    fun refresh() {
        loadCities()
    }
}

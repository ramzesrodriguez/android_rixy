package com.externalpods.rixy.feature.user.cityhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.CityHome
import com.externalpods.rixy.core.model.CitySection
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.domain.usecase.city.GetCityHomeUseCase
import com.externalpods.rixy.navigation.AppStateViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CityHomeUiState(
    val city: City? = null,
    val featured: Listing? = null,
    val feed: List<Listing> = emptyList(),
    val sections: List<CitySection> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class CityHomeViewModel(
    private val getCityHomeUseCase: GetCityHomeUseCase,
    private val appState: AppStateViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(CityHomeUiState())
    val uiState: StateFlow<CityHomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appState.selectedCity.collect { city ->
                city?.let { loadCityHome(it.slug) }
            }
        }
    }

    fun loadCityHome(citySlug: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getCityHomeUseCase(citySlug)
                .onSuccess { cityHome ->
                    _uiState.update { state ->
                        state.copy(
                            city = cityHome.city,
                            featured = cityHome.featured,
                            feed = cityHome.feed,
                            sections = cityHome.sections,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun refresh() {
        _uiState.value.city?.let { city ->
            _uiState.update { it.copy(isRefreshing = true) }
            loadCityHome(city.slug)
        }
    }

    fun retry() {
        _uiState.value.city?.let { loadCityHome(it.slug) }
    }
}

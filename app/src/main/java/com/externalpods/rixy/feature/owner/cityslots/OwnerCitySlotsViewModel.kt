package com.externalpods.rixy.feature.owner.cityslots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.CitySlotSubscription
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.navigation.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OwnerCitySlotsUiState(
    val subscriptions: List<CitySlotSubscription> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatingCheckout: Boolean = false,
    val error: String? = null,
    val checkoutUrl: String? = null
)

class OwnerCitySlotsViewModel(
    private val ownerRepository: OwnerRepository,
    private val appState: AppState
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerCitySlotsUiState())
    val uiState: StateFlow<OwnerCitySlotsUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptions()
    }

    fun loadSubscriptions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val subscriptions = ownerRepository.getCitySlots()
                _uiState.update { 
                    it.copy(
                        subscriptions = subscriptions,
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

    fun cancelSubscription(subscriptionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                ownerRepository.cancelCitySlot(subscriptionId)
                loadSubscriptions()
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

    fun onCheckoutCompleted() {
        _uiState.update { it.copy(checkoutUrl = null) }
        loadSubscriptions()
    }

    fun onCheckoutCancelled() {
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

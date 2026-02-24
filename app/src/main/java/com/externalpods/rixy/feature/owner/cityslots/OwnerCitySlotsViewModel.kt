package com.externalpods.rixy.feature.owner.cityslots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.CitySlotSubscription
import com.externalpods.rixy.core.model.CitySlotType
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.core.model.CitySlot
import com.externalpods.rixy.navigation.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AvailableSlot(
    val cityId: String,
    val cityName: String,
    val type: CitySlotType,
    val slotIndex: Int,
    val basePriceCents: Int,
    val currency: String
)

data class OwnerCitySlotsUiState(
    val subscriptions: List<CitySlotSubscription> = emptyList(),
    val availableSlots: List<AvailableSlot> = emptyList(), // Available slots for purchase
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

    fun purchaseSlot(slot: AvailableSlot, listingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCheckout = true, error = null) }
            
            try {
                val citySlot = CitySlot(
                    cityId = slot.cityId,
                    type = slot.type,
                    slotIndex = slot.slotIndex,
                    listingId = listingId
                )
                val response = ownerRepository.purchaseSlot(citySlot)
                response.checkoutUrl?.let { url ->
                    _uiState.update { it.copy(checkoutUrl = url, isCreatingCheckout = false) }
                } ?: run {
                    _uiState.update { it.copy(isCreatingCheckout = false, error = "No checkout URL received") }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCreatingCheckout = false,
                        error = e.message
                    )
                }
            }
        }
    }
}

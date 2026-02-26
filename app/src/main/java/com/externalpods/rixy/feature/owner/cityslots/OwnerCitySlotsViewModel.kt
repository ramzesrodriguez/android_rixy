package com.externalpods.rixy.feature.owner.cityslots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.CitySlotSubscription
import com.externalpods.rixy.core.model.CitySlotType
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.core.model.CitySlot
import com.externalpods.rixy.navigation.AppStateViewModel
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
    val ownerListings: List<Listing> = emptyList(),
    val pendingPurchaseSlot: AvailableSlot? = null,
    val showListingPicker: Boolean = false,
    val isLoading: Boolean = false,
    val isCreatingCheckout: Boolean = false,
    val error: String? = null,
    val checkoutUrl: String? = null
)

class OwnerCitySlotsViewModel(
    private val ownerRepository: OwnerRepository,
    private val appState: AppStateViewModel
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
                val listings = ownerRepository.getListings()
                    .filter { it.isActive == true }

                val selectedCityId = appState.selectedCity.value?.id
                val cityIdForAvailability = selectedCityId ?: subscriptions.firstOrNull()?.cityId

                val availableSlots = if (cityIdForAvailability.isNullOrBlank()) {
                    emptyList()
                } else {
                    val availability = ownerRepository.getCitySlotAvailability(cityIdForAvailability)
                    val pricingByType = availability.pricing.associateBy { it.slotType }
                    availability.slotTypesAvailability.flatMap { slotTypeAvailability ->
                        slotTypeAvailability.availableIndices.map { index ->
                            val pricing = pricingByType[slotTypeAvailability.slotType]
                            AvailableSlot(
                                cityId = cityIdForAvailability,
                                cityName = appState.selectedCity.value?.name
                                    ?: subscriptions.firstOrNull { it.cityId == cityIdForAvailability }?.cityName
                                    ?: "Ciudad",
                                type = slotTypeAvailability.slotType,
                                slotIndex = index,
                                basePriceCents = pricing?.basePriceCents ?: slotTypeAvailability.basePriceCents,
                                currency = pricing?.currency ?: slotTypeAvailability.currency
                            )
                        }
                    }
                }

                _uiState.update { 
                    it.copy(
                        subscriptions = subscriptions,
                        ownerListings = listings,
                        availableSlots = availableSlots,
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

    fun openListingPickerForSlot(slot: AvailableSlot) {
        _uiState.update {
            it.copy(
                pendingPurchaseSlot = slot,
                showListingPicker = true
            )
        }
    }

    fun dismissListingPicker() {
        _uiState.update {
            it.copy(
                pendingPurchaseSlot = null,
                showListingPicker = false
            )
        }
    }

    fun purchaseSelectedSlot(listingId: String) {
        val slot = _uiState.value.pendingPurchaseSlot ?: return
        dismissListingPicker()
        purchaseSlot(slot, listingId)
    }

    private fun purchaseSlot(slot: AvailableSlot, listingId: String) {
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

package com.externalpods.rixy.feature.owner.cityslots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.CitySlotSubscription
import com.externalpods.rixy.core.model.CitySlotType
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.PublicCitySlot
import com.externalpods.rixy.core.network.dto.CitySlotActionRequest
import com.externalpods.rixy.core.model.SlotDetail
import com.externalpods.rixy.data.repository.CityRepository
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.core.model.CitySlot
import com.externalpods.rixy.navigation.AppStateViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Individual slot info for display
data class SlotInfo(
    val index: Int,
    val isAvailable: Boolean,
    val businessName: String? = null,
    val endAt: String? = null,
    val subscriptionId: String? = null
)

// Grouped slots by type
data class SlotTypeGroup(
    val type: CitySlotType,
    val description: String,
    val basePriceCents: Int,
    val currency: String,
    val totalSlots: Int,
    val slots: List<SlotInfo>
) {
    val formattedPrice: String get() = "${currency}$${basePriceCents / 100.0}"
}

data class OwnerCitySlotsUiState(
    val subscriptions: List<CitySlotSubscription> = emptyList(),
    val publicSlots: List<PublicCitySlot> = emptyList(),
    val availableSlots: List<AvailableSlot> = emptyList(),
    val slotTypeGroups: List<SlotTypeGroup> = emptyList(),
    val ownerListings: List<Listing> = emptyList(),
    val cities: List<City> = emptyList(),
    val filteredCities: List<City> = emptyList(),
    val selectedCity: City? = null,
    val citySearchQuery: String = "",
    val isLoadingCities: Boolean = false,
    val pendingPurchaseSlot: AvailableSlot? = null,
    val showListingPicker: Boolean = false,
    val showCityPicker: Boolean = false,
    val isLoading: Boolean = false,
    val isCreatingCheckout: Boolean = false,
    val error: String? = null,
    val checkoutUrl: String? = null,
    val selectedCityName: String = ""
)

class OwnerCitySlotsViewModel(
    private val ownerRepository: OwnerRepository,
    private val cityRepository: CityRepository,
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
                val business = ownerRepository.getBusiness()
                val listings = ownerRepository.getListings()
                    .filter { it.isActive == true }
                val subscriptions = runCatching { ownerRepository.getCitySlotSubscriptions() }
                    .getOrDefault(emptyList())

                val selectedCity = appState.selectedCity.value
                val cityIdForCheckout = selectedCity?.id ?: business?.cityId ?: subscriptions.firstOrNull()?.cityId
                val citySlugForAvailability = selectedCity?.slug ?: business?.city?.slug
                val citySlugForPublicSlots = selectedCity?.slug ?: business?.city?.slug
                val cityName = selectedCity?.name ?: business?.city?.name ?: "Ciudad"
                
                // Store the selected city in state for the picker
                _uiState.update { it.copy(selectedCity = selectedCity) }

                // Load slot availability and public slots
                val availability = if (citySlugForAvailability.isNullOrBlank() || cityIdForCheckout.isNullOrBlank()) {
                    null
                } else {
                    ownerRepository.getCitySlotAvailability(citySlugForAvailability)
                }
                
                val publicSlots = if (citySlugForPublicSlots.isNullOrBlank()) {
                    emptyList()
                } else {
                    cityRepository.getCitySlots(citySlugForPublicSlots)
                }

                // Create pricing map
                val pricingByType = availability?.pricing?.associateBy { it.slotType } ?: emptyMap()
                
                // Create slot type groups from availability.slots and pricing
                val slotTypeGroups = availability?.slots?.mapNotNull { slotItem ->
                    val pricing = pricingByType[slotItem.slotType]
                    if (pricing == null) return@mapNotNull null
                    
                    // Build slot info list
                    val slotInfos = slotItem.slots.map { detail ->
                        SlotInfo(
                            index = detail.index,
                            isAvailable = detail.isAvailable,
                            businessName = detail.currentSubscription?.businessName,
                            endAt = detail.currentSubscription?.endAt,
                            subscriptionId = detail.currentSubscription?.id
                        )
                    }.sortedBy { it.index }
                    
                    SlotTypeGroup(
                        type = slotItem.slotType,
                        description = getDefaultDescription(slotItem.slotType),
                        basePriceCents = pricing.basePriceCents,
                        currency = pricing.currency,
                        totalSlots = pricing.totalSlots,
                        slots = slotInfos
                    )
                }?.sortedBy { it.type.name } ?: emptyList()

                // Create available slots for purchase (legacy format for compatibility)
                val availableSlots = availability?.slots?.flatMap { slotItem ->
                    val pricing = pricingByType[slotItem.slotType]
                    slotItem.slots
                        .filter { it.isAvailable }
                        .map { slotDetail ->
                            AvailableSlot(
                                cityId = cityIdForCheckout ?: "",
                                cityName = cityName,
                                type = slotItem.slotType,
                                slotIndex = slotDetail.index,
                                basePriceCents = pricing?.basePriceCents ?: 0,
                                currency = pricing?.currency ?: "MXN"
                            )
                        }
                } ?: emptyList()

                _uiState.update { 
                    it.copy(
                        subscriptions = subscriptions,
                        publicSlots = publicSlots,
                        ownerListings = listings,
                        availableSlots = availableSlots,
                        slotTypeGroups = slotTypeGroups,
                        selectedCityName = cityName,
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

    private fun getDefaultDescription(type: CitySlotType): String {
        return when (type) {
            CitySlotType.HOME_HERO_SPOTLIGHT -> "Prime hero position at the top of the homepage"
            CitySlotType.HOME_HORIZONTAL_CAROUSEL -> "Scrolling carousel section on homepage"
            CitySlotType.HOME_CATEGORY_RAIL -> "Category rail section on homepage"
            CitySlotType.HOME_GRID_1 -> "Grid section position 1 on homepage"
            CitySlotType.HOME_GRID_2 -> "Grid section position 2 on homepage"
            CitySlotType.HOME_EVENTS_STRIP -> "Events strip section on homepage"
            CitySlotType.HOME_NEW_ARRIVALS -> "New arrivals section on homepage"
            CitySlotType.HOME_FEATURED_PLACEMENT -> "Featured placement section on homepage"
            CitySlotType.UNKNOWN -> "City slot"
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

    fun retryPayment(subscriptionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCheckout = true, error = null) }
            
            try {
                val actionRequest = CitySlotActionRequest(
                    successUrl = "rixy://payment/success?type=slot&id=$subscriptionId&session_id={CHECKOUT_SESSION_ID}",
                    cancelUrl = "rixy://payment/cancel?type=slot&id=$subscriptionId"
                )
                val response = ownerRepository.retryCitySlotPayment(subscriptionId, actionRequest)
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

    fun renewSubscription(subscriptionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCheckout = true, error = null) }
            
            try {
                val actionRequest = CitySlotActionRequest(
                    successUrl = "rixy://payment/success?type=slot&id=$subscriptionId&session_id={CHECKOUT_SESSION_ID}",
                    cancelUrl = "rixy://payment/cancel?type=slot&id=$subscriptionId"
                )
                val response = ownerRepository.renewCitySlotSubscription(subscriptionId, actionRequest)
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

    fun onCheckoutCompleted() {
        _uiState.update { it.copy(checkoutUrl = null) }
        loadSubscriptions()
    }

    fun onCheckoutCancelled() {
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    fun onCheckoutStarted() {
        // Clear the checkout URL to prevent re-triggering the same URL
        _uiState.update { it.copy(checkoutUrl = null) }
    }

    fun onCheckoutError(errorMessage: String) {
        _uiState.update { it.copy(checkoutUrl = null, error = errorMessage) }
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

    // City picker functions
    fun showCityPicker() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCities = true) }
            try {
                // Load cities if not already loaded
                if (_uiState.value.cities.isEmpty()) {
                    val cities = cityRepository.getCities()
                    _uiState.update { 
                        it.copy(
                            cities = cities,
                            filteredCities = cities,
                            showCityPicker = true,
                            isLoadingCities = false
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            filteredCities = it.cities,
                            showCityPicker = true,
                            isLoadingCities = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoadingCities = false) }
            }
        }
    }

    fun dismissCityPicker() {
        _uiState.update { it.copy(showCityPicker = false, citySearchQuery = "") }
    }

    fun onCitySearchQueryChange(query: String) {
        val allCities = _uiState.value.cities
        val filtered = if (query.isBlank()) {
            allCities
        } else {
            allCities.filter { city ->
                city.name.contains(query, ignoreCase = true) ||
                city.state?.contains(query, ignoreCase = true) == true
            }
        }
        _uiState.update { it.copy(citySearchQuery = query, filteredCities = filtered) }
    }

    fun selectCity(city: City) {
        viewModelScope.launch {
            // Update app state with selected city
            appState.selectCity(city)
            
            _uiState.update { 
                it.copy(
                    selectedCity = city,
                    selectedCityName = city.name,
                    showCityPicker = false,
                    citySearchQuery = ""
                )
            }
            
            // Reload data for new city
            loadSubscriptions()
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

data class AvailableSlot(
    val cityId: String,
    val cityName: String,
    val type: CitySlotType,
    val slotIndex: Int,
    val basePriceCents: Int,
    val currency: String
)

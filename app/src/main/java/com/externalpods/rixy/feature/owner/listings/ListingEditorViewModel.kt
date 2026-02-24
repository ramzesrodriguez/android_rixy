package com.externalpods.rixy.feature.owner.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import com.externalpods.rixy.core.model.DeliveryOptions
import com.externalpods.rixy.core.model.ProductAttributes
import com.externalpods.rixy.core.network.dto.CreateListingRequest
import com.externalpods.rixy.core.network.dto.EventDetailsInput
import com.externalpods.rixy.core.network.dto.ProductDetailsInput
import com.externalpods.rixy.core.network.dto.ServiceDetailsInput
import com.externalpods.rixy.core.network.dto.UpdateListingRequest
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.service.ImageUploadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListingEditorUiState(
    // Step 1: Type selection
    val currentStep: Int = 1,
    val selectedType: ListingType? = null,
    
    // Step 2: Basic info
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val photoUrls: List<String> = emptyList(),
    
    // Step 3: Type-specific details
    // Product
    val productPrice: String = "",
    val productCurrency: String = "MXN",
    val stockStatus: com.externalpods.rixy.core.model.StockStatus = com.externalpods.rixy.core.model.StockStatus.IN_STOCK,
    val stockQuantity: String = "",
    val condition: com.externalpods.rixy.core.model.Condition? = null,
    val pickupAvailable: Boolean = false,
    val deliveryAvailable: Boolean = false,
    val shippingAvailable: Boolean = false,
    
    // Service
    val servicePrice: String = "",
    val servicePriceType: com.externalpods.rixy.core.model.PricingModel = com.externalpods.rixy.core.model.PricingModel.FIXED,
    val durationMinutes: String = "",
    val serviceAreaType: com.externalpods.rixy.core.model.ServiceAreaType = com.externalpods.rixy.core.model.ServiceAreaType.ON_SITE,
    
    // Event
    val eventStartDate: String = "",
    val eventEndDate: String = "",
    val venueName: String = "",
    val eventPrice: String = "",
    val capacity: String = "",
    val isOnline: Boolean = false,
    
    // Common
    val existingListing: Listing? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class ListingEditorViewModel(
    private val ownerRepository: OwnerRepository,
    private val imageUploadService: ImageUploadService,
    private val listingId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListingEditorUiState())
    val uiState: StateFlow<ListingEditorUiState> = _uiState.asStateFlow()

    init {
        listingId?.let { loadListing(it) }
    }

    private fun loadListing(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val listing = ownerRepository.getListing(id)
                _uiState.update { state ->
                    state.copy(
                        existingListing = listing,
                        selectedType = listing.type,
                        title = listing.title,
                        description = listing.description ?: "",
                        category = listing.categoryTag ?: "",
                        photoUrls = listing.photoUrls ?: emptyList(),
                        // Product details
                        productPrice = listing.productDetails?.priceAmount ?: "",
                        stockQuantity = listing.productDetails?.stockQuantity?.toString() ?: "",
                        condition = listing.productDetails?.condition,
                        pickupAvailable = listing.productDetails?.deliveryOptions?.pickup == true,
                        deliveryAvailable = listing.productDetails?.deliveryOptions?.delivery == true,
                        shippingAvailable = listing.productDetails?.deliveryOptions?.shipping == true,
                        // Service details
                        servicePrice = listing.serviceDetails?.priceAmount ?: "",
                        durationMinutes = listing.serviceDetails?.durationMinutes?.toString() ?: "",
                        serviceAreaType = listing.serviceDetails?.serviceAreaType ?: com.externalpods.rixy.core.model.ServiceAreaType.ON_SITE,
                        // Event details
                        eventStartDate = listing.eventDetails?.startAt ?: "",
                        eventEndDate = listing.eventDetails?.endAt ?: "",
                        venueName = listing.eventDetails?.venueName ?: "",
                        eventPrice = listing.eventDetails?.priceAmount ?: "",
                        capacity = listing.eventDetails?.capacity?.toString() ?: "",
                        isOnline = listing.eventDetails?.isOnline == true,
                        isLoading = false,
                        currentStep = 2
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onTypeSelected(type: ListingType) {
        _uiState.update { it.copy(selectedType = type, currentStep = 2) }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value) }
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true) }
            
            val currentUrls = _uiState.value.photoUrls.toMutableList()
            
            imageUploadService.uploadImage(uri)
                .onSuccess { url ->
                    currentUrls.add(url)
                    _uiState.update { it.copy(photoUrls = currentUrls, isUploadingImage = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUploadingImage = false, error = error.message) }
                }
        }
    }

    fun removeImage(url: String) {
        _uiState.update { state ->
            state.copy(photoUrls = state.photoUrls.filter { it != url })
        }
    }

    fun goToStep(step: Int) {
        _uiState.update { it.copy(currentStep = step) }
    }

    fun saveListing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                val state = _uiState.value
                
                // Build request based on type
                val request = when (state.selectedType) {
                    ListingType.PRODUCT -> CreateListingRequest(
                        type = ListingType.PRODUCT,
                        title = state.title,
                        description = state.description.takeIf { it.isNotBlank() },
                        categoryTag = state.category.takeIf { it.isNotBlank() },
                        photoUrls = state.photoUrls,
                        productDetails = ProductDetailsInput(
                            priceAmount = state.productPrice.toDoubleOrNull() ?: 0.0,
                            currency = state.productCurrency,
                            stockStatus = state.stockStatus,
                            stockQuantity = state.stockQuantity.toIntOrNull(),
                            condition = state.condition,
                            deliveryOptions = DeliveryOptions(
                                pickup = state.pickupAvailable,
                                delivery = state.deliveryAvailable,
                                shipping = state.shippingAvailable
                            )
                        )
                    )
                    ListingType.SERVICE -> CreateListingRequest(
                        type = ListingType.SERVICE,
                        title = state.title,
                        description = state.description.takeIf { it.isNotBlank() },
                        categoryTag = state.category.takeIf { it.isNotBlank() },
                        photoUrls = state.photoUrls,
                        serviceDetails = ServiceDetailsInput(
                            pricingModel = state.servicePriceType,
                            priceAmount = state.servicePrice.toDoubleOrNull(),
                            durationMinutes = state.durationMinutes.toIntOrNull(),
                            serviceAreaType = state.serviceAreaType
                        )
                    )
                    ListingType.EVENT -> CreateListingRequest(
                        type = ListingType.EVENT,
                        title = state.title,
                        description = state.description.takeIf { it.isNotBlank() },
                        categoryTag = state.category.takeIf { it.isNotBlank() },
                        photoUrls = state.photoUrls,
                        eventDetails = EventDetailsInput(
                            startAt = state.eventStartDate,
                            endAt = state.eventEndDate.takeIf { it.isNotBlank() },
                            venueName = state.venueName.takeIf { it.isNotBlank() },
                            priceAmount = state.eventPrice.toDoubleOrNull(),
                            capacity = state.capacity.toIntOrNull(),
                            isOnline = state.isOnline
                        )
                    )
                    null -> throw IllegalStateException("No listing type selected")
                }
                
                if (state.existingListing != null) {
                    // Update existing
                    val updateRequest = UpdateListingRequest(
                        title = state.title,
                        description = state.description.takeIf { it.isNotBlank() },
                        categoryTag = state.category.takeIf { it.isNotBlank() },
                        photoUrls = state.photoUrls
                        // Note: Type-specific details update not implemented in UpdateListingRequest
                    )
                    ownerRepository.updateListing(state.existingListing.id, updateRequest)
                } else {
                    // Create new
                    ownerRepository.createListing(request)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

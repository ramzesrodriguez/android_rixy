package com.externalpods.rixy.feature.owner.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.network.dto.CreateBusinessRequest
import com.externalpods.rixy.core.network.dto.UpdateBusinessRequest
import com.externalpods.rixy.data.repository.OwnerRepository
import com.externalpods.rixy.service.ImageUploadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri

data class BusinessEditorUiState(
    val business: Business? = null,
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val website: String = "",
    val logoUrl: String? = null,
    val headerUrl: String? = null,
    val openingHours: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class BusinessEditorViewModel(
    private val ownerRepository: OwnerRepository,
    private val imageUploadService: ImageUploadService
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessEditorUiState())
    val uiState: StateFlow<BusinessEditorUiState> = _uiState.asStateFlow()

    init {
        loadBusiness()
    }

    private fun loadBusiness() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val business = ownerRepository.getBusiness()
                business?.let {
                    _uiState.update { state ->
                        state.copy(
                            business = it,
                            name = it.name,
                            description = it.description ?: "",
                            address = it.addressText ?: "",
                            phone = it.phone ?: "",
                            whatsapp = it.whatsapp ?: "",
                            website = it.website ?: "",
                            logoUrl = it.logoUrl,
                            headerUrl = it.headerImageUrl,
                            openingHours = it.openingHoursText ?: "",
                            isLoading = false
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(isLoading = false) }
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

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onAddressChange(value: String) {
        _uiState.update { it.copy(address = value) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun onWhatsappChange(value: String) {
        _uiState.update { it.copy(whatsapp = value) }
    }

    fun onWebsiteChange(value: String) {
        _uiState.update { it.copy(website = value) }
    }

    fun onOpeningHoursChange(value: String) {
        _uiState.update { it.copy(openingHours = value) }
    }

    fun uploadLogo(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true) }
            
            imageUploadService.uploadImage(uri, filename = "logo_${System.currentTimeMillis()}.jpg")
                .onSuccess { url ->
                    _uiState.update { it.copy(logoUrl = url, isUploadingImage = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUploadingImage = false, error = error.message) }
                }
        }
    }

    fun uploadHeader(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true) }
            
            imageUploadService.uploadImage(uri, filename = "header_${System.currentTimeMillis()}.jpg")
                .onSuccess { url ->
                    _uiState.update { it.copy(headerUrl = url, isUploadingImage = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isUploadingImage = false, error = error.message) }
                }
        }
    }

    fun saveBusiness() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                val state = _uiState.value
                
                if (state.business == null) {
                    // Create new business
                    // Note: cityId should come from selected city or user preference
                    val request = CreateBusinessRequest(
                        cityId = "", // TODO: Get from app state
                        name = state.name,
                        description = state.description.takeIf { it.isNotBlank() },
                        addressText = state.address.takeIf { it.isNotBlank() },
                        phone = state.phone.takeIf { it.isNotBlank() },
                        whatsapp = state.whatsapp.takeIf { it.isNotBlank() },
                        website = state.website.takeIf { it.isNotBlank() },
                        logoUrl = state.logoUrl,
                        headerImageUrl = state.headerUrl
                    )
                    ownerRepository.createBusiness(request)
                } else {
                    // Update existing
                    val request = UpdateBusinessRequest(
                        name = state.name,
                        description = state.description.takeIf { it.isNotBlank() },
                        addressText = state.address.takeIf { it.isNotBlank() },
                        phone = state.phone.takeIf { it.isNotBlank() },
                        whatsapp = state.whatsapp.takeIf { it.isNotBlank() },
                        website = state.website.takeIf { it.isNotBlank() },
                        logoUrl = state.logoUrl,
                        headerImageUrl = state.headerUrl
                    )
                    ownerRepository.updateBusiness(request)
                }
                
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onSaveSuccessDismissed() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

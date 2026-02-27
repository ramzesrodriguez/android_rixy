package com.externalpods.rixy.feature.owner.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OwnerListingsUiState(
    val listings: List<Listing> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OwnerListingsViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerListingsUiState())
    val uiState: StateFlow<OwnerListingsUiState> = _uiState.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val listings = ownerRepository.getListings()
                _uiState.update { 
                    it.copy(
                        listings = listings,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar publicaciones"
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val listings = ownerRepository.getListings()
                _uiState.update { 
                    it.copy(
                        listings = listings,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

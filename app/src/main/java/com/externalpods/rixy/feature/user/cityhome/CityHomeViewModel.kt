package com.externalpods.rixy.feature.user.cityhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.CitySection
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.PublicCitySlot
import com.externalpods.rixy.data.repository.CityRepository
import com.externalpods.rixy.domain.usecase.city.GetCityHomeUseCase
import com.externalpods.rixy.navigation.AppStateViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

data class CityHomeUiState(
    val city: City? = null,
    val featured: Listing? = null,
    val feed: List<Listing> = emptyList(),
    val sections: List<CitySection> = emptyList(),
    val sectionItems: Map<String, List<Listing>> = emptyMap(),
    val slots: List<PublicCitySlot> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class CityHomeViewModel(
    private val getCityHomeUseCase: GetCityHomeUseCase,
    private val cityRepository: CityRepository,
    private val appState: AppStateViewModel
) : ViewModel() {
    private companion object {
        const val TAG = "CityHomeViewModel"
    }

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
                    val cityInfo = runCatching {
                        cityRepository.getCityInfo(citySlug)
                    }.getOrNull()

                    val sections = runCatching {
                        cityRepository.getCitySections(citySlug)
                    }.getOrElse { emptyList() }

                    val sectionItems = sections.associate { section ->
                        section.id to runCatching {
                            val maxItems = (section.configJson?.get("maxItems") as? JsonPrimitive)?.intOrNull
                            cityRepository.getCitySectionItems(
                                citySlug = citySlug,
                                sectionKey = section.key,
                                limit = maxItems
                            )
                        }.getOrElse { emptyList() }
                    }

                    val slots = runCatching {
                        cityRepository.getCitySlots(citySlug)
                    }.getOrElse { emptyList() }

                    val baseCity = cityInfo ?: cityHome.city
                    val heroCity = applyHeroCountFallbacks(
                        city = baseCity,
                        featured = cityHome.featured,
                        feed = cityHome.feed,
                        slots = slots
                    )

                    Log.d(
                        TAG,
                        "Loaded city=$citySlug homeFeed=${cityHome.feed.size} sections=${sections.size} sectionItems=${sectionItems.values.sumOf { it.size }} slots=${slots.size}"
                    )

                    _uiState.update { state ->
                        state.copy(
                            city = heroCity,
                            featured = cityHome.featured,
                            feed = cityHome.feed,
                            sections = sections.ifEmpty { cityHome.sections },
                            sectionItems = sectionItems,
                            slots = slots,
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

    private fun applyHeroCountFallbacks(
        city: City,
        featured: Listing?,
        feed: List<Listing>,
        slots: List<PublicCitySlot>
    ): City {
        val businessIds = mutableSetOf<String>()
        feed.mapNotNullTo(businessIds) { it.businessId }
        featured?.businessId?.let { businessIds.add(it) }
        slots.forEach { slot ->
            val businessId = slot.listing?.business?.id ?: slot.business?.id
            if (!businessId.isNullOrBlank()) businessIds.add(businessId)
        }

        val listingIds = mutableSetOf<String>()
        feed.mapTo(listingIds) { it.id }
        featured?.id?.let { listingIds.add(it) }
        slots.forEach { slot ->
            slot.listing?.id?.let { listingIds.add(it) }
        }

        val derivedBusinessCount = businessIds.size.takeIf { it > 0 }
        val derivedListingCount = listingIds.size.takeIf { it > 0 }
        val derivedSubscriptionCount = slots.size.takeIf { it > 0 }

        return city.copy(
            totalBusinesses = city.totalBusinesses ?: city.businessCount ?: derivedBusinessCount,
            totalListings = city.totalListings ?: city.listingCount ?: derivedListingCount,
            subscriptionsCount = city.subscriptionsCount
                ?: city.citySlotSubscriptionsCount
                ?: city.citySlotSubscriptions?.size
                ?: derivedSubscriptionCount
        )
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

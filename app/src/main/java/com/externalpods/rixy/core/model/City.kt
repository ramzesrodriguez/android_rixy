package com.externalpods.rixy.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class City(
    val id: String,
    val name: String,
    val slug: String,
    val heroImageUrl: String? = null,
    val isActive: Boolean? = null,
    val isPublishingEnabled: Boolean? = null,
    val isAdsEnabled: Boolean? = null,
    val timezone: String? = null,
    val country: String? = null,
    val state: String? = null,
    val businessCount: Int? = null,
    val listingCount: Int? = null,
    val userCount: Int? = null,
    val totalBusinesses: Int? = null,
    val totalListings: Int? = null,
    val totalUsers: Int? = null,
    val citySlotSubscriptions: List<CitySlotSubscriptionCountItem>? = null,
    val citySlotSubscriptionsCount: Int? = null,
    val subscriptionsCount: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    // Computed properties (matching iOS resolvedXxx pattern)
    val resolvedBusinessCount: Int get() = businessCount ?: totalBusinesses ?: 0
    val resolvedListingCount: Int get() = listingCount ?: totalListings ?: 0
    val resolvedUserCount: Int get() = userCount ?: totalUsers ?: 0
    val resolvedSubscriptionCount: Int get() =
        citySlotSubscriptionsCount ?: subscriptionsCount ?: citySlotSubscriptions?.size ?: 0

    // iOS-compatible computed properties (nullable)
    val resolvedBusinessCountOrNull: Int? get() = businessCount ?: totalBusinesses
    val resolvedListingCountOrNull: Int? get() = listingCount ?: totalListings
    val resolvedSubscriptionCountOrNull: Int? get() =
        citySlotSubscriptionsCount ?: subscriptionsCount ?: citySlotSubscriptions?.size

    // Helper properties
    val hasActivePublishing: Boolean get() = isPublishingEnabled == true
    val hasActiveAds: Boolean get() = isAdsEnabled == true
    val isActiveCity: Boolean get() = isActive == true
    val displayLocation: String get() = listOfNotNull(name, state, country).joinToString(", ")
}

@Serializable
data class CitySlotSubscriptionCountItem(
    val id: String
)

@Serializable
data class CitySummary(
    val id: String? = null,
    val name: String? = null,
    val slug: String? = null
)

@Serializable
data class CitySection(
    val id: String,
    val cityId: String? = null,
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val type: CitySectionType,
    val order: Int,
    val isActive: Boolean? = null,
    val configJson: Map<String, JsonElement>? = null
) {
    // Helper computed properties
    val isVisible: Boolean get() = isActive ?: true
    val hasConfig: Boolean get() = !configJson.isNullOrEmpty()
}

@Serializable
data class CityHome(
    val city: City,
    val featured: Listing? = null,
    val feed: List<Listing> = emptyList(),
    val sections: List<CitySection> = emptyList()
) {
    // Safe accessors (matching iOS pattern)
    val safeFeed: List<Listing> get() = feed
    val safeSections: List<CitySection> get() = sections
    val hasFeatured: Boolean get() = featured != null
    val hasSections: Boolean get() = sections.isNotEmpty()
}

@Serializable
data class CitySectionItemsResponse(
    val section: CitySection,
    val items: List<Listing> = emptyList()
)

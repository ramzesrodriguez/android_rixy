package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class City(
    val id: String,
    val name: String,
    val slug: String,
    @SerialName("hero_image_url")
    @JsonNames("heroImageUrl")
    val heroImageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("is_publishing_enabled") val isPublishingEnabled: Boolean? = null,
    @SerialName("is_ads_enabled") val isAdsEnabled: Boolean? = null,
    val timezone: String? = null,
    val country: String? = null,
    val state: String? = null,
    @SerialName("business_count") val businessCount: Int? = null,
    @SerialName("listing_count") val listingCount: Int? = null,
    @SerialName("user_count") val userCount: Int? = null,
    @SerialName("total_businesses") val totalBusinesses: Int? = null,
    @SerialName("total_listings") val totalListings: Int? = null,
    @SerialName("total_users") val totalUsers: Int? = null,
    @SerialName("city_slot_subscriptions") val citySlotSubscriptions: List<CitySlotSubscriptionCountItem>? = null,
    @SerialName("city_slot_subscriptions_count") val citySlotSubscriptionsCount: Int? = null,
    @SerialName("subscriptions_count") val subscriptionsCount: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
) {
    // Computed properties (matching iOS resolvedXxx pattern)
    val resolvedBusinessCount: Int get() = businessCount ?: totalBusinesses ?: 0
    val resolvedListingCount: Int get() = listingCount ?: totalListings ?: 0
    val resolvedUserCount: Int get() = userCount ?: totalUsers ?: 0
    val resolvedSubscriptionCount: Int get() = 
        citySlotSubscriptionsCount ?: subscriptionsCount ?: citySlotSubscriptions?.size ?: 0
    
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
    val id: String,
    val name: String,
    val slug: String
)

@Serializable
data class CitySection(
    val id: String,
    @SerialName("city_id") val cityId: String,
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val type: CitySectionType,
    val order: Int,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("config_json") val configJson: Map<String, JsonElement>? = null
) {
    // Helper computed properties
    val isVisible: Boolean get() = isActive
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

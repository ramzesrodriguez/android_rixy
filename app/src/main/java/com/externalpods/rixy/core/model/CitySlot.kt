package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class CitySlotSubscription(
    val id: String,
    @SerialName("city_id")
    @JsonNames("cityId")
    val cityId: String,
    @SerialName("owner_id")
    @JsonNames("ownerId")
    val ownerId: String,
    @SerialName("business_id")
    @JsonNames("businessId")
    val businessId: String,
    @SerialName("slot_type")
    @JsonNames("slotType")
    val slotType: CitySlotType,
    val status: CitySlotStatus,
    @SerialName("slot_index")
    @JsonNames("slotIndex")
    val slotIndex: Int,
    @SerialName("start_at")
    @JsonNames("startAt")
    val startAt: String,
    @SerialName("end_at")
    @JsonNames("endAt")
    val endAt: String,
    @SerialName("amount_cents")
    @JsonNames("amountCents")
    val amountCents: Int,
    val currency: String,
    @SerialName("created_at")
    @JsonNames("createdAt")
    val createdAt: String,
    val city: CitySummary? = null,
    val business: BusinessSummary? = null,
    val assignment: CitySlotAssignment? = null,
    @SerialName("days_remaining")
    @JsonNames("daysRemaining")
    val daysRemaining: Int? = null
) {
    // Computed properties (matching iOS)
    val isActive: Boolean get() = status == CitySlotStatus.ACTIVE
    val isExpired: Boolean get() = status == CitySlotStatus.EXPIRED
    val isPending: Boolean get() = status == CitySlotStatus.PENDING
    val isPaused: Boolean get() = status == CitySlotStatus.PAUSED
    val isExpiringSoon: Boolean get() = (daysRemaining ?: 0) <= 3 && (daysRemaining ?: 0) > 0
    val formattedPrice: String get() = "${amountCents / 100.0} $currency"
    
    // Helper property for city name
    val cityName: String get() = city?.name ?: "Unknown City"
}

@Serializable
data class CitySlotAssignment(
    val id: String,
    @SerialName("city_id")
    @JsonNames("cityId")
    val cityId: String,
    @SerialName("subscription_id")
    @JsonNames("subscriptionId")
    val subscriptionId: String,
    @SerialName("listing_id")
    @JsonNames("listingId")
    val listingId: String,
    @SerialName("slot_type")
    @JsonNames("slotType")
    val slotType: CitySlotType,
    @SerialName("slot_index")
    @JsonNames("slotIndex")
    val slotIndex: Int,
    @SerialName("is_active")
    @JsonNames("isActive")
    val isActive: Boolean,
    @SerialName("clicks_count")
    @JsonNames("clicksCount")
    val clicksCount: Int = 0,
    @SerialName("views_count")
    @JsonNames("viewsCount")
    val viewsCount: Int = 0,
    val listing: ListingSummary? = null
)

// Public-facing city slot (used in city home)
@Serializable
data class PublicCitySlot(
    val id: String,
    @SerialName("city_id")
    @JsonNames("cityId")
    val cityId: String,
    @SerialName("subscription_id")
    @JsonNames("subscriptionId")
    val subscriptionId: String? = null,
    @SerialName("listing_id")
    @JsonNames("listingId")
    val listingId: String? = null,
    @SerialName("slot_type")
    @JsonNames("slotType")
    val slotType: CitySlotType,
    @SerialName("slot_index")
    @JsonNames("slotIndex")
    val slotIndex: Int,
    @SerialName("is_active")
    @JsonNames("isActive")
    val isActive: Boolean,
    val priority: Int? = null,
    @SerialName("clicks_count")
    @JsonNames("clicksCount")
    val clicksCount: Int = 0,
    @SerialName("views_count")
    @JsonNames("viewsCount")
    val viewsCount: Int = 0,
    @SerialName("created_at")
    @JsonNames("createdAt")
    val createdAt: String? = null,
    @SerialName("updated_at")
    @JsonNames("updatedAt")
    val updatedAt: String? = null,
    val subscription: SlotSubscription? = null,
    val listing: SlotListing? = null,
    val business: SlotBusiness? = null
) {
    // Computed properties (matching iOS)
    val hasContent: Boolean get() = isActive && listing != null
    val engagementRate: Float get() = if (viewsCount > 0) clicksCount.toFloat() / viewsCount else 0f
}

@Serializable
data class SlotSubscription(
    val id: String,
    @SerialName("start_at")
    @JsonNames("startAt")
    val startAt: String? = null,
    @SerialName("end_at")
    @JsonNames("endAt")
    val endAt: String? = null,
    @SerialName("status")
    @JsonNames("status")
    val status: CitySlotStatus? = null
)

@Serializable
data class SlotListing(
    val id: String,
    val title: String,
    val type: ListingType,
    @SerialName("photo_urls")
    @JsonNames("photoUrls")
    val photoUrls: List<String> = emptyList(),
    val description: String? = null,
    @SerialName("category_tag")
    @JsonNames("categoryTag")
    val categoryTag: String? = null,
    val business: SlotBusiness? = null,
    @SerialName("price_amount")
    @JsonNames("priceAmount")
    val priceAmount: String? = null,
    val currency: String? = null,
    @SerialName("price_type")
    @JsonNames("priceType")
    val priceType: PriceType? = null
)

@Serializable
data class SlotBusiness(
    val id: String,
    val name: String,
    @SerialName("logo_url")
    @JsonNames("logoUrl")
    val logoUrl: String? = null
)

// Slot availability models (owner purchasing flow)
@Serializable
data class CitySlotAvailabilityResponse(
    val slots: List<SlotAvailabilityItem> = emptyList(),
    val pricing: List<SlotPricingItem> = emptyList(),
    @SerialName("slot_types_availability")
    @JsonNames("slotTypesAvailability")
    val slotTypesAvailability: List<SlotTypeAvailability> = emptyList()
)

@Serializable
data class SlotAvailabilityItem(
    @SerialName("slot_type")
    @JsonNames("slotType")
    val slotType: CitySlotType,
    val slots: List<SlotDetail> = emptyList()
)

@Serializable
data class SlotDetail(
    val index: Int,
    @SerialName("is_available")
    @JsonNames("isAvailable")
    val isAvailable: Boolean,
    @SerialName("current_subscription")
    @JsonNames("currentSubscription")
    val currentSubscription: CurrentSlotSubscription? = null
)

@Serializable
data class CurrentSlotSubscription(
    val id: String,
    @SerialName("business_id")
    @JsonNames("businessId")
    val businessId: String,
    @SerialName("business_name")
    @JsonNames("businessName")
    val businessName: String? = null,
    @SerialName("end_at")
    @JsonNames("endAt")
    val endAt: String
)

@Serializable
data class SlotPricingItem(
    @SerialName("slot_type")
    @JsonNames("slotType")
    val slotType: CitySlotType,
    @SerialName("base_price_cents")
    @JsonNames("basePriceCents")
    val basePriceCents: Int,
    val currency: String,
    @SerialName("available_slots")
    @JsonNames("availableSlots")
    val availableSlots: Int,
    @SerialName("total_slots")
    @JsonNames("totalSlots")
    val totalSlots: Int
) {
    val formattedPrice: String get() = "${basePriceCents / 100.0} $currency"
}

@Serializable
data class SlotTypeAvailability(
    @SerialName("slot_type") val slotType: CitySlotType,
    @SerialName("slot_description") val slotDescription: String? = null,
    @SerialName("total_slots") val totalSlots: Int,
    @SerialName("base_price_cents") val basePriceCents: Int,
    val currency: String,
    @SerialName("available_indices") val availableIndices: List<Int> = emptyList(),
    @SerialName("occupied_indices") val occupiedIndices: List<Int> = emptyList(),
    val unconfigured: Boolean = false
) {
    // Computed properties (matching iOS)
    val isAvailable: Boolean get() = availableIndices.isNotEmpty()
    val isConfigured: Boolean get() = !unconfigured && basePriceCents > 0
    val occupancyRate: Float get() = if (totalSlots > 0) occupiedIndices.size.toFloat() / totalSlots else 0f
}

// CitySlot model for purchase flow (defined separately for repository use)
data class CitySlot(
    val cityId: String,
    val type: CitySlotType,
    val slotIndex: Int,
    val listingId: String? = null
)

@Serializable
data class CitySlotCheckoutResponse(
    @SerialName("subscription_id") val subscriptionId: String? = null,
    @SerialName("session_id") val sessionId: String? = null,
    @SerialName("checkout_url") val checkoutUrl: String? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    @SerialName("amount_cents") val amountCents: Int? = null,
    val currency: String? = null,
    @SerialName("client_secret") val clientSecret: String? = null,
    @SerialName("publishable_key") val publishableKey: String? = null
) {
    val formattedPrice: String get() = "${(amountCents ?: 0) / 100.0} ${currency ?: "MXN"}"
}

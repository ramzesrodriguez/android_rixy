package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeaturedPlacement(
    val id: String,
    @SerialName("city_id") val cityId: String,
    @SerialName("listing_id") val listingId: String,
    @SerialName("business_id") val businessId: String,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    val status: FeaturedPlacementStatus,
    @SerialName("created_at") val createdAt: String,
    val listing: ListingSummary? = null,
    val city: CitySummary? = null,
    @SerialName("queue_position") val queuePosition: Int? = null,
    @SerialName("payment_transactions") val paymentTransactions: List<PaymentTransaction>? = null,
    // Additional fields for UI compatibility
    @SerialName("listing_title") val listingTitle: String? = null,
    @SerialName("amount_cents") val amountCents: Int? = null,
    val currency: String? = null,
    @SerialName("slot_type") val slotType: CitySlotType? = null,
    @SerialName("base_price_cents") val basePriceCents: Int? = null
) {
    // Helper computed properties
    val displayTitle: String get() = listingTitle ?: listing?.title ?: "Unknown Listing"
}

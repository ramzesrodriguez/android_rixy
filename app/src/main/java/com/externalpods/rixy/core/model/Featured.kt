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
    @SerialName("payment_transactions") val paymentTransactions: List<PaymentTransaction>? = null
)

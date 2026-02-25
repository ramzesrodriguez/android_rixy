package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Listing(
    val id: String,
    @SerialName("city_id") val cityId: String? = null,
    @SerialName("business_id") val businessId: String? = null,
    @SerialName("owner_id") val ownerId: String? = null,
    val type: ListingType,
    val title: String,
    val slug: String? = null,
    val description: String? = null,
    @SerialName("category_tag") val categoryTag: String? = null,
    @SerialName("photo_urls")
    @JsonNames("photoUrls")
    val photoUrls: List<String>? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    val status: ListingStatus? = null,
    @SerialName("created_at")
    @JsonNames("createdAt")
    val createdAt: String? = null,
    @SerialName("updated_at")
    @JsonNames("updatedAt")
    val updatedAt: String? = null,
    @SerialName("product_details") val productDetails: ProductDetails? = null,
    @SerialName("service_details") val serviceDetails: ServiceDetails? = null,
    @SerialName("event_details") val eventDetails: EventDetails? = null,
    val business: BusinessSummary? = null,
    @SerialName("payment_transactions") val paymentTransactions: List<PaymentTransaction>? = null,
    @SerialName("is_featured") val isFeatured: Boolean? = null
)

@Serializable
data class ListingSummary(
    val id: String,
    val title: String,
    val type: ListingType,
    @SerialName("photo_urls")
    @JsonNames("photoUrls")
    val photoUrls: List<String> = emptyList()
)

@Serializable
data class ProductDetails(
    @SerialName("listing_id") val listingId: String? = null,
    @SerialName("price_amount") val priceAmount: String? = null,
    val currency: String? = null,
    @SerialName("price_type") val priceType: PriceType? = null,
    @SerialName("compare_at_price_amount") val compareAtPriceAmount: String? = null,
    @SerialName("stock_status") val stockStatus: StockStatus? = null,
    @SerialName("stock_quantity") val stockQuantity: Int? = null,
    val sku: String? = null,
    val brand: String? = null,
    val condition: Condition? = null,
    val attributes: ProductAttributes? = null,
    @SerialName("delivery_options") val deliveryOptions: DeliveryOptions? = null
)

@Serializable
data class ProductAttributes(
    val colors: List<String>? = null,
    val sizes: List<String>? = null,
    val material: String? = null
)

@Serializable
data class DeliveryOptions(
    val pickup: Boolean = false,
    val delivery: Boolean = false,
    val shipping: Boolean = false
)

@Serializable
data class ServiceDetails(
    @SerialName("listing_id") val listingId: String? = null,
    @SerialName("pricing_model") val pricingModel: PricingModel? = null,
    @SerialName("price_amount") val priceAmount: String? = null,
    val currency: String? = null,
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
    @SerialName("service_area_type") val serviceAreaType: ServiceAreaType? = null,
    @SerialName("service_area_text") val serviceAreaText: String? = null,
    @SerialName("availability_text") val availabilityText: String? = null,
    @SerialName("booking_url") val bookingUrl: String? = null,
    val requirements: String? = null
)

@Serializable
data class EventDetails(
    @SerialName("listing_id") val listingId: String? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    val timezone: String? = null,
    @SerialName("venue_name") val venueName: String? = null,
    @SerialName("address_text") val addressText: String? = null,
    @SerialName("map_url") val mapUrl: String? = null,
    @SerialName("ticket_url") val ticketUrl: String? = null,
    @SerialName("price_amount") val priceAmount: String? = null,
    val currency: String? = null,
    val capacity: Int? = null,
    @SerialName("is_online") val isOnline: Boolean? = null,
    @SerialName("online_url") val onlineUrl: String? = null,
    @SerialName("age_restriction") val ageRestriction: String? = null,
    @SerialName("event_status") val eventStatus: EventStatus? = null
)

@Serializable
data class PaymentTransaction(
    val id: String,
    @SerialName("checkout_session_id") val checkoutSessionId: String? = null,
    val status: PaymentStatus? = null,
    @SerialName("amount_cents") val amountCents: Int? = null,
    val currency: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

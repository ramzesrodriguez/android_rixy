package com.externalpods.rixy.core.network.dto

import com.externalpods.rixy.core.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// --- Analytics ---

@Serializable
data class TrackViewRequest(
    @SerialName("entity_type") val entityType: String,
    @SerialName("entity_id") val entityId: String,
    val action: String = "VIEW"
)

// --- Business ---

@Serializable
data class CreateBusinessRequest(
    @SerialName("city_id") val cityId: String,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("header_image_url") val headerImageUrl: String? = null,
    @SerialName("address_text") val addressText: String? = null,
    @SerialName("map_url") val mapUrl: String? = null,
    @SerialName("opening_hours_text") val openingHoursText: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val website: String? = null
)

@Serializable
data class UpdateBusinessRequest(
    val name: String? = null,
    val slug: String? = null,
    val description: String? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("header_image_url") val headerImageUrl: String? = null,
    @SerialName("address_text") val addressText: String? = null,
    @SerialName("map_url") val mapUrl: String? = null,
    @SerialName("opening_hours_text") val openingHoursText: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val website: String? = null,
    @SerialName("city_id") val cityId: String? = null
)

// --- Listings ---

@Serializable
data class CreateListingRequest(
    val type: ListingType,
    val title: String,
    val slug: String? = null,
    val description: String? = null,
    @SerialName("category_tag") val categoryTag: String? = null,
    @SerialName("photo_urls") val photoUrls: List<String> = emptyList(),
    @SerialName("product_details") val productDetails: ProductDetailsInput? = null,
    @SerialName("service_details") val serviceDetails: ServiceDetailsInput? = null,
    @SerialName("event_details") val eventDetails: EventDetailsInput? = null
)

@Serializable
data class UpdateListingRequest(
    val title: String? = null,
    val slug: String? = null,
    val description: String? = null,
    @SerialName("category_tag") val categoryTag: String? = null,
    @SerialName("photo_urls") val photoUrls: List<String>? = null,
    @SerialName("product_details") val productDetails: ProductDetailsInput? = null,
    @SerialName("service_details") val serviceDetails: ServiceDetailsInput? = null,
    @SerialName("event_details") val eventDetails: EventDetailsInput? = null
)

@Serializable
data class ProductDetailsInput(
    @SerialName("price_amount") val priceAmount: Double,
    val currency: String = "MXN",
    @SerialName("price_type") val priceType: PriceType = PriceType.FIXED,
    @SerialName("compare_at_price_amount") val compareAtPriceAmount: Double? = null,
    @SerialName("stock_status") val stockStatus: StockStatus = StockStatus.IN_STOCK,
    @SerialName("stock_quantity") val stockQuantity: Int? = null,
    val sku: String? = null,
    val brand: String? = null,
    val condition: Condition? = null,
    val attributes: ProductAttributes? = null,
    @SerialName("delivery_options") val deliveryOptions: DeliveryOptions
)

@Serializable
data class ServiceDetailsInput(
    @SerialName("pricing_model") val pricingModel: PricingModel,
    @SerialName("price_amount") val priceAmount: Double? = null,
    val currency: String? = null,
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
    @SerialName("service_area_type") val serviceAreaType: ServiceAreaType = ServiceAreaType.ON_SITE,
    @SerialName("service_area_text") val serviceAreaText: String? = null,
    @SerialName("availability_text") val availabilityText: String? = null,
    @SerialName("booking_url") val bookingUrl: String? = null,
    val requirements: String? = null
)

@Serializable
data class EventDetailsInput(
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String? = null,
    val timezone: String? = null,
    @SerialName("venue_name") val venueName: String? = null,
    @SerialName("address_text") val addressText: String? = null,
    @SerialName("map_url") val mapUrl: String? = null,
    @SerialName("ticket_url") val ticketUrl: String? = null,
    @SerialName("price_amount") val priceAmount: Double? = null,
    val currency: String? = null,
    val capacity: Int? = null,
    @SerialName("is_online") val isOnline: Boolean = false,
    @SerialName("online_url") val onlineUrl: String? = null,
    @SerialName("age_restriction") val ageRestriction: String? = null
)

// --- Favorites ---

@Serializable
data class FavoriteRequest(
    @SerialName("listing_id") val listingId: String
)

// --- Uploads ---

@Serializable
data class PresignUploadRequest(
    val filename: String,
    @SerialName("content_type") val contentType: String
)

@Serializable
data class PresignUploadResponse(
    @SerialName("upload_url") val uploadUrl: String,
    @SerialName("public_url") val publicUrl: String,
    val fields: Map<String, String>? = null
)

// --- Featured ---

@Serializable
data class FeaturedCheckoutRequest(
    @SerialName("listing_id") val listingId: String,
    @SerialName("success_url") val successUrl: String = DEFAULT_SUCCESS_URL,
    @SerialName("cancel_url") val cancelUrl: String = DEFAULT_CANCEL_URL
)

@Serializable
data class CheckoutResponse(
    @SerialName("client_secret") val clientSecret: String? = null,
    @SerialName("session_id") val sessionId: String? = null,
    @SerialName("checkout_url") val checkoutUrl: String? = null,
    @SerialName("publishable_key") val publishableKey: String? = null,
    @SerialName("payment_intent_id") val paymentIntentId: String? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    @SerialName("amount_cents") val amountCents: Int? = null,
    val currency: String? = null
)

// --- City Slots ---

@Serializable
data class CreateCitySlotCheckoutRequest(
    @SerialName("city_id") val cityId: String,
    @SerialName("slot_type") val slotType: CitySlotType,
    @SerialName("slot_index") val slotIndex: Int,
    @SerialName("listing_id") val listingId: String,
    @SerialName("business_id") val businessId: String? = null,
    @SerialName("success_url") val successUrl: String = DEFAULT_CITY_SLOT_SUCCESS_URL,
    @SerialName("cancel_url") val cancelUrl: String = DEFAULT_CITY_SLOT_CANCEL_URL
)

@Serializable
data class CitySlotActionRequest(
    @SerialName("success_url") val successUrl: String = DEFAULT_CITY_SLOT_SUCCESS_URL,
    @SerialName("cancel_url") val cancelUrl: String = DEFAULT_CITY_SLOT_CANCEL_URL
)

@Serializable
data class CancelSlotRequest(
    @SerialName("reason_code") val reasonCode: String? = "OWNER_CANCELED",
    val note: String? = null
)

// --- Business Sections ---

@Serializable
data class CreateBusinessSectionRequest(
    @SerialName("city_id") val cityId: String,
    val title: String,
    val subtitle: String? = null,
    val type: BusinessSectionType,
    @SerialName("entity_type") val entityType: String,
    @SerialName("item_ids") val itemIds: List<String> = emptyList(),
    val order: Int = 0,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_premium") val isPremium: Boolean = false
)

@Serializable
data class UpdateBusinessSectionRequest(
    val title: String? = null,
    val subtitle: String? = null,
    @SerialName("item_ids") val itemIds: List<String>? = null,
    val order: Int? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("is_premium") val isPremium: Boolean? = null
)

// --- Admin ---

@Serializable
data class CreateCityRequest(
    val name: String,
    val slug: String,
    val timezone: String = "America/Mexico_City",
    val country: String? = null,
    val state: String? = null
)

@Serializable
data class UpdateCityRequest(
    val name: String? = null,
    val slug: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("is_publishing_enabled") val isPublishingEnabled: Boolean? = null,
    @SerialName("is_ads_enabled") val isAdsEnabled: Boolean? = null,
    val timezone: String? = null,
    val country: String? = null,
    val state: String? = null
)

@Serializable
data class CreateCitySectionRequest(
    @SerialName("city_id") val cityId: String,
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val type: CitySectionType,
    val order: Int = 0,
    @SerialName("config_json") val configJson: Map<String, JsonElement>? = null
)

@Serializable
data class UpdateCitySectionRequest(
    val title: String? = null,
    val subtitle: String? = null,
    val order: Int? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("config_json") val configJson: Map<String, JsonElement>? = null
)

@Serializable
data class ModerateRequest(
    val action: ModerationAction,
    @SerialName("reason_code") val reasonCode: String? = null,
    val note: String? = null
)

@Serializable
data class UpdateRoleRequest(
    val role: OwnerRole
)

@Serializable
data class UpdatePricingRequest(
    @SerialName("slot_type") val slotType: CitySlotType,
    @SerialName("base_price_cents") val basePriceCents: Int,
    val currency: String = "MXN"
)

// Deep link constants (matching iOS)
private const val DEFAULT_SUCCESS_URL = "rixy://payment/success?session_id={CHECKOUT_SESSION_ID}"
private const val DEFAULT_CANCEL_URL = "rixy://payment/cancel"
private const val DEFAULT_CITY_SLOT_SUCCESS_URL = "rixy://owner/city-slots?checkout=success&session_id={CHECKOUT_SESSION_ID}"
private const val DEFAULT_CITY_SLOT_CANCEL_URL = "rixy://owner/city-slots?checkout=cancel"

package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Business(
    val id: String,
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("city_id") val cityId: String? = null,
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
    val website: String? = null,
    val status: BusinessStatus? = null,
    @SerialName("moderation_reason_code") val moderationReasonCode: String? = null,
    @SerialName("moderation_note") val moderationNote: String? = null,
    @SerialName("moderated_at") val moderatedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val city: CitySummary? = null,
    @SerialName("owner_name") val ownerName: String? = null
)

@Serializable
data class BusinessSummary(
    val id: String,
    val name: String,
    @SerialName("logo_url") val logoUrl: String? = null
)

@Serializable
data class BusinessSection(
    val id: String,
    @SerialName("business_id") val businessId: String,
    @SerialName("city_id") val cityId: String,
    val title: String,
    val subtitle: String? = null,
    val type: BusinessSectionType,
    @SerialName("entity_type") val entityType: String,
    @SerialName("item_ids") val itemIds: List<String> = emptyList(),
    val order: Int,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_premium") val isPremium: Boolean
)

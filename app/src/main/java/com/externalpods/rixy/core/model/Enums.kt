package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AppMode {
    USER, OWNER, ADMIN
}

@Serializable
enum class OwnerRole {
    @SerialName("OWNER") OWNER,
    @SerialName("ADMIN") ADMIN
}

@Serializable
enum class OwnerStatus {
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("SUSPENDED") SUSPENDED,
    @SerialName("BANNED") BANNED
}

@Serializable
enum class BusinessStatus {
    @SerialName("PENDING_REVIEW") PENDING_REVIEW,
    @SerialName("APPROVED") APPROVED,
    @SerialName("REJECTED") REJECTED,
    @SerialName("SUSPENDED") SUSPENDED
}

@Serializable
enum class ListingType {
    @SerialName("PRODUCT") PRODUCT,
    @SerialName("SERVICE") SERVICE,
    @SerialName("EVENT") EVENT;
    
    // Display helpers (matching iOS)
    val displayName: String
        get() = when (this) {
            PRODUCT -> "Producto"
            SERVICE -> "Servicio"
            EVENT -> "Evento"
        }
    
    val icon: String
        get() = when (this) {
            PRODUCT -> "🛍️"
            SERVICE -> "🔧"
            EVENT -> "🎉"
        }
}

@Serializable
enum class ListingStatus {
    @SerialName("DRAFT") DRAFT,
    @SerialName("PENDING_REVIEW") PENDING_REVIEW,
    @SerialName("PUBLISHED") PUBLISHED,
    @SerialName("REJECTED") REJECTED,
    @SerialName("SUSPENDED") SUSPENDED
}

@Serializable
enum class PriceType {
    @SerialName("FIXED") FIXED,
    @SerialName("FROM") FROM,
    @SerialName("RANGE") RANGE,
    @SerialName("FREE") FREE;
    
    val displayName: String
        get() = when (this) {
            FIXED -> "Precio fijo"
            FROM -> "Desde"
            RANGE -> "Rango"
            FREE -> "Gratis"
        }
}

@Serializable
enum class StockStatus {
    @SerialName("IN_STOCK") IN_STOCK,
    @SerialName("LIMITED") LIMITED,
    @SerialName("OUT_OF_STOCK") OUT_OF_STOCK,
    @SerialName("PREORDER") PREORDER;
    
    val displayName: String
        get() = when (this) {
            IN_STOCK -> "En stock"
            LIMITED -> "Stock limitado"
            OUT_OF_STOCK -> "Agotado"
            PREORDER -> "Preventa"
        }
}

@Serializable
enum class Condition {
    @SerialName("NEW") NEW,
    @SerialName("USED") USED,
    @SerialName("REFURBISHED") REFURBISHED;
    
    val displayName: String
        get() = when (this) {
            NEW -> "Nuevo"
            USED -> "Usado"
            REFURBISHED -> "Reacondicionado"
        }
}

@Serializable
enum class PricingModel {
    @SerialName("FIXED") FIXED,
    @SerialName("FROM") FROM,
    @SerialName("HOURLY") HOURLY,
    @SerialName("QUOTE") QUOTE;
    
    val displayName: String
        get() = when (this) {
            FIXED -> "Precio fijo"
            FROM -> "Desde"
            HOURLY -> "Por hora"
            QUOTE -> "A cotizar"
        }
}

@Serializable
enum class ServiceAreaType {
    @SerialName("ON_SITE") ON_SITE,
    @SerialName("REMOTE") REMOTE,
    @SerialName("BOTH") BOTH;
    
    val displayName: String
        get() = when (this) {
            ON_SITE -> "En sitio"
            REMOTE -> "Remoto"
            BOTH -> "Ambos"
        }
}

@Serializable
enum class EventStatus {
    @SerialName("SCHEDULED") SCHEDULED,
    @SerialName("CANCELED") CANCELED,
    @SerialName("POSTPONED") POSTPONED
}

@Serializable
enum class FeaturedPlacementStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("EXPIRED") EXPIRED,
    @SerialName("CANCELED") CANCELED
}

@Serializable
enum class CitySlotStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("EXPIRED") EXPIRED,
    @SerialName("CANCELED") CANCELED,
    @SerialName("PAUSED") PAUSED,
    @SerialName("UNKNOWN") UNKNOWN;  // Fallback for unknown values
    
    companion object {
        fun fromString(value: String): CitySlotStatus = 
            values().find { it.name == value.uppercase() } ?: UNKNOWN
    }
}

@Serializable
enum class CitySlotType {
    @SerialName("HOME_HERO_SPOTLIGHT") HOME_HERO_SPOTLIGHT,
    @SerialName("HOME_HORIZONTAL_CAROUSEL") HOME_HORIZONTAL_CAROUSEL,
    @SerialName("HOME_CATEGORY_RAIL") HOME_CATEGORY_RAIL,
    @SerialName("HOME_GRID_1") HOME_GRID_1,
    @SerialName("HOME_GRID_2") HOME_GRID_2,
    @SerialName("HOME_EVENTS_STRIP") HOME_EVENTS_STRIP,
    @SerialName("HOME_NEW_ARRIVALS") HOME_NEW_ARRIVALS,
    @SerialName("HOME_FEATURED_PLACEMENT") HOME_FEATURED_PLACEMENT,
    @SerialName("UNKNOWN") UNKNOWN;  // Fallback for unknown values
    
    val displayName: String
        get() = when (this) {
            HOME_HERO_SPOTLIGHT -> "Hero"
            HOME_HORIZONTAL_CAROUSEL -> "Carrusel"
            HOME_CATEGORY_RAIL -> "Categorías"
            HOME_GRID_1 -> "Grid 1"
            HOME_GRID_2 -> "Grid 2"
            HOME_EVENTS_STRIP -> "Eventos"
            HOME_NEW_ARRIVALS -> "Nuevos"
            HOME_FEATURED_PLACEMENT -> "Destacados"
            UNKNOWN -> "Slot"
        }
    
    companion object {
        fun fromString(value: String): CitySlotType = 
            values().find { it.name == value.uppercase() } ?: UNKNOWN
    }
}

@Serializable
enum class PaymentStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("PAID") PAID,
    @SerialName("FAILED") FAILED,
    @SerialName("CANCELED") CANCELED
}

@Serializable
enum class CitySectionType {
    @SerialName("CITY_HERO_SPOTLIGHT") CITY_HERO_SPOTLIGHT,
    @SerialName("CITY_CATEGORY_RAIL") CITY_CATEGORY_RAIL,
    @SerialName("CITY_TOP_BUSINESSES") CITY_TOP_BUSINESSES,
    @SerialName("CITY_DEALS_GRID") CITY_DEALS_GRID,
    @SerialName("CITY_EVENTS_STRIP") CITY_EVENTS_STRIP,
    @SerialName("CITY_NEW_ARRIVALS") CITY_NEW_ARRIVALS,
    @SerialName("CITY_FEATURED_PLACEMENT") CITY_FEATURED_PLACEMENT,
    @SerialName("UNKNOWN") UNKNOWN;  // Fallback for unknown values
    
    companion object {
        fun fromString(value: String): CitySectionType = 
            values().find { it.name == value.uppercase() } ?: UNKNOWN
    }
}

@Serializable
enum class BusinessSectionType {
    @SerialName("HORIZONTAL_CAROUSEL") HORIZONTAL_CAROUSEL,
    @SerialName("GRID") GRID,
    @SerialName("FEATURE_CARD") FEATURE_CARD,
    @SerialName("PROMO_BANNER") PROMO_BANNER
}

@Serializable
enum class ModerationAction {
    @SerialName("APPROVE") APPROVE,
    @SerialName("REJECT") REJECT,
    @SerialName("SUSPEND") SUSPEND
}

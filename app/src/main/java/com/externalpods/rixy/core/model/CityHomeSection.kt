package com.externalpods.rixy.core.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a section in the city home screen UI
 */
@Serializable
data class CityHomeSection(
    val id: String,
    val title: String,
    val displayType: String,
    val listings: List<Listing> = emptyList()
)

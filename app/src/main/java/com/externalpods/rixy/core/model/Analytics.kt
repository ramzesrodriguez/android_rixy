package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerAnalyticsOverview(
    @SerialName("rangeDays") val rangeDays: Int,
    val totals: AnalyticsTotals,
    @SerialName("listingViewsByType") val listingViewsByType: ListingViewsByType,
    @SerialName("topListings") val topListings: List<TopListing> = emptyList()
)

@Serializable
data class AnalyticsTotals(
    @SerialName("businessViews") val businessViews: Int = 0,
    @SerialName("listingViews") val listingViews: Int = 0,
    @SerialName("totalViews") val totalViews: Int = 0,
    @SerialName("uniqueVisitors") val uniqueVisitors: Int = 0,
    @SerialName("returningVisitors") val returningVisitors: Int = 0,
    @SerialName("avgDwellMs") val avgDwellMs: Int = 0
)

@Serializable
data class ListingViewsByType(
    val product: Int = 0,
    val service: Int = 0,
    val event: Int = 0
)

@Serializable
data class TopListing(
    val id: String,
    val title: String,
    val type: ListingType,
    val status: ListingStatus,
    val views: Int
)

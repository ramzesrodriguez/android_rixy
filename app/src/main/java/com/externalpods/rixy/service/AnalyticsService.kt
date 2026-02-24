package com.externalpods.rixy.service

import com.externalpods.rixy.core.network.PublicApiService
import com.externalpods.rixy.core.network.dto.TrackViewRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalyticsService(
    private val publicApi: PublicApiService
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun trackView(citySlug: String, entityType: String, entityId: String) {
        scope.launch {
            try {
                publicApi.trackView(citySlug, TrackViewRequest(entityType, entityId))
            } catch (_: Exception) {
                // Fire and forget — silently fail
            }
        }
    }

    fun trackSlotView(assignmentId: String) {
        scope.launch {
            try {
                publicApi.trackSlotView(assignmentId)
            } catch (_: Exception) {
                // Fire and forget
            }
        }
    }

    fun trackSlotClick(assignmentId: String) {
        scope.launch {
            try {
                publicApi.trackSlotClick(assignmentId)
            } catch (_: Exception) {
                // Fire and forget
            }
        }
    }
}

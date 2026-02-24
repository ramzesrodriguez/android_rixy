package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AuditLog(
    val id: String,
    @SerialName("actor_owner_id") val actorOwnerId: String,
    val action: String,
    @SerialName("entity_type") val entityType: String,
    @SerialName("entity_id") val entityId: String,
    @SerialName("city_id") val cityId: String? = null,
    @SerialName("reason_code") val reasonCode: String? = null,
    val note: String? = null,
    @SerialName("before_json") val beforeJson: Map<String, JsonElement>? = null,
    @SerialName("after_json") val afterJson: Map<String, JsonElement>? = null,
    @SerialName("created_at") val createdAt: String
) {
    // Helper properties
    val hasChanges: Boolean get() = !beforeJson.isNullOrEmpty() || !afterJson.isNullOrEmpty()
    val isModerationAction: Boolean get() = action in setOf("APPROVE", "REJECT", "SUSPEND")
    val isCreateAction: Boolean get() = action == "CREATE"
    val isUpdateAction: Boolean get() = action == "UPDATE"
    val isDeleteAction: Boolean get() = action == "DELETE"
    
    // Alias for actorOwnerId for compatibility
    val actorId: String get() = actorOwnerId
}

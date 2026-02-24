package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val id: String,
    val email: String,
    val role: OwnerRole,
    val status: OwnerStatus,
    @SerialName("session_version") val sessionVersion: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val business: Business? = null
)

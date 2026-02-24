package com.externalpods.rixy.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val success: Boolean? = null,
    val message: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val data: List<T> = emptyList(),
    @SerialName("next_cursor") val nextCursor: String? = null,
    val pagination: PaginationInfo? = null
)

@Serializable
data class PaginationInfo(
    val total: Int = 0,
    val limit: Int = 0,
    val offset: Int = 0
)

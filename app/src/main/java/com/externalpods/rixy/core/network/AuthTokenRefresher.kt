package com.externalpods.rixy.core.network

import android.util.Log
import com.externalpods.rixy.data.local.TokenManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Base64

@Serializable
private data class SupabaseRefreshResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String? = null
)

class AuthTokenRefresher(
    private val tokenManager: TokenManager
) {
    private val refreshClient = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    /** True when we know the refresh token is permanently invalid. */
    @Volatile
    private var refreshPermanentlyFailed = false

    /** Reset the failure flag (e.g. after a fresh login). */
    fun resetRefreshState() {
        refreshPermanentlyFailed = false
    }

    @Synchronized
    fun refreshAccessToken(): String? {
        if (refreshPermanentlyFailed) {
            Log.w(TAG, "Refresh permanently failed, skipping (user must re-login)")
            return null
        }

        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            Log.w(TAG, "No refresh token available")
            return null
        }

        Log.d(TAG, "Attempting token refresh via Supabase HTTP...")

        val requestBody = """{"refresh_token":"$refreshToken"}"""
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${ApiConfig.SUPABASE_URL}/auth/v1/token?grant_type=refresh_token")
            .addHeader("apikey", ApiConfig.SUPABASE_KEY)
            .addHeader("Authorization", "Bearer ${ApiConfig.SUPABASE_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return try {
            refreshClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    Log.e(TAG, "Token refresh failed: ${response.code} - $errorBody")

                    // Permanent failures: token revoked, already used, or not found
                    if (errorBody.contains("refresh_token_already_used", ignoreCase = true) ||
                        errorBody.contains("refresh_token_not_found", ignoreCase = true) ||
                        errorBody.contains("invalid_grant", ignoreCase = true)
                    ) {
                        Log.e(TAG, "Refresh token permanently invalid — clearing session")
                        refreshPermanentlyFailed = true
                        tokenManager.clearToken()
                    }
                    return null
                }
                val body = response.body?.string() ?: return null
                val refreshResponse = runCatching {
                    json.decodeFromString<SupabaseRefreshResponse>(body)
                }.getOrNull() ?: return null

                tokenManager.saveSession(
                    accessToken = refreshResponse.accessToken,
                    refreshToken = refreshResponse.refreshToken ?: refreshToken
                )
                Log.d(TAG, "Token refreshed successfully")
                return refreshResponse.accessToken
            }
        } catch (e: Exception) {
            Log.e(TAG, "Token refresh exception: ${e.message}")
            null
        }
    }

    @Synchronized
    fun refreshAccessTokenWithFallback(): String? {
        return refreshAccessToken()
    }

    fun getFreshAccessToken(currentToken: String?): String? {
        if (currentToken.isNullOrBlank()) return null
        if (refreshPermanentlyFailed) return null
        if (!isTokenExpiredOrNearExpiry(currentToken)) return currentToken
        return refreshAccessToken() ?: currentToken
    }

    private companion object {
        const val TAG = "AuthTokenRefresher"
    }

    private fun isTokenExpiredOrNearExpiry(token: String, leewaySeconds: Long = 60): Boolean {
        val parts = token.split(".")
        if (parts.size < 2) return true

        val payloadJson = runCatching {
            val padded = parts[1].padEnd(((parts[1].length + 3) / 4) * 4, '=')
            String(Base64.getUrlDecoder().decode(padded))
        }.getOrNull() ?: return true

        val exp = runCatching {
            json.parseToJsonElement(payloadJson)
                .jsonObject["exp"]
                ?.jsonPrimitive
                ?.content
                ?.toLong()
        }.getOrNull() ?: return true

        val nowSeconds = System.currentTimeMillis() / 1000
        return exp <= nowSeconds + leewaySeconds
    }
}

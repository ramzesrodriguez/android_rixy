package com.externalpods.rixy.core.network

import com.externalpods.rixy.data.local.TokenManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

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
    private val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = ApiConfig.SUPABASE_URL,
        supabaseKey = ApiConfig.SUPABASE_KEY
    ) {
        install(Auth)
    }

    @Synchronized
    fun refreshAccessToken(): String? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val requestBody = """{"refresh_token":"$refreshToken"}"""
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${ApiConfig.SUPABASE_URL}/auth/v1/token?grant_type=refresh_token")
            .addHeader("apikey", ApiConfig.SUPABASE_KEY)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        refreshClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val refreshResponse = runCatching {
                json.decodeFromString<SupabaseRefreshResponse>(body)
            }.getOrNull() ?: return null

            tokenManager.saveSession(
                accessToken = refreshResponse.accessToken,
                refreshToken = refreshResponse.refreshToken ?: refreshToken
            )
            return refreshResponse.accessToken
        }
    }

    @Synchronized
    fun refreshAccessTokenWithFallback(): String? {
        // 1) Preferred: explicit refresh token persisted in secure storage.
        refreshAccessToken()?.let { return it }

        // 2) Fallback: use Supabase persisted session (covers migrated users).
        val refreshed = runBlocking {
            runCatching {
                supabase.auth.refreshCurrentSession()
                supabase.auth.currentSessionOrNull()
            }.getOrNull()
        } ?: return null

        val accessToken = refreshed.accessToken
        tokenManager.saveSession(
            accessToken = accessToken,
            refreshToken = refreshed.refreshToken
        )
        return accessToken
    }
}

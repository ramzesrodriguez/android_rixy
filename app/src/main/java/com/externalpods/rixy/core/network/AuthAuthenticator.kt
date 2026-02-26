package com.externalpods.rixy.core.network

import com.externalpods.rixy.data.local.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AuthAuthenticator(
    private val tokenManager: TokenManager,
    private val tokenRefresher: AuthTokenRefresher
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val authHeader = response.request.header("Authorization") ?: return null
        if (!authHeader.startsWith("Bearer ")) return null
        if (responseCount(response) >= 2) return null

        synchronized(this) {
            val requestToken = authHeader.removePrefix("Bearer ").trim()
            val latestToken = tokenManager.getToken()

            // Another request might have already refreshed the token.
            if (!latestToken.isNullOrBlank() && latestToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestToken")
                    .build()
            }

            val refreshedToken = tokenRefresher.refreshAccessTokenWithFallback() ?: return null
            return response.request.newBuilder()
                .header("Authorization", "Bearer $refreshedToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

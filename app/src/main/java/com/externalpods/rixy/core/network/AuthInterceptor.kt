package com.externalpods.rixy.core.network

import com.externalpods.rixy.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val tokenRefresher: AuthTokenRefresher
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalToken = tokenManager.getToken()
        val requestToken = tokenRefresher.getFreshAccessToken(originalToken)
        val requestBuilder = originalRequest.newBuilder()

        requestToken?.let { token ->
            requestBuilder.header("Authorization", "Bearer $token")
        }

        requestBuilder.header("Content-Type", "application/json")

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        if (
            response.code != 401 ||
            request.header(RETRY_HEADER) == RETRY_HEADER_VALUE ||
            requestToken.isNullOrBlank() ||
            !isTokenInvalidResponse(response)
        ) {
            return response
        }

        val refreshedToken = tokenRefresher.refreshAccessTokenWithFallback()
        if (refreshedToken.isNullOrBlank() || refreshedToken == requestToken) {
            return response
        }

        response.close()
        val retriedRequest = request.newBuilder()
            .header("Authorization", "Bearer $refreshedToken")
            .header(RETRY_HEADER, RETRY_HEADER_VALUE)
            .build()
        return chain.proceed(retriedRequest)
    }

    private fun isTokenInvalidResponse(response: Response): Boolean {
        val body = runCatching { response.peekBody(1024 * 1024).string() }.getOrNull().orEmpty()
        return body.contains("TOKEN_INVALID", ignoreCase = true) ||
            body.contains("expired token", ignoreCase = true) ||
            body.contains("invalid token", ignoreCase = true)
    }

    private companion object {
        const val RETRY_HEADER = "X-Auth-Retry"
        const val RETRY_HEADER_VALUE = "true"
    }
}

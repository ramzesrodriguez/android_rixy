package com.externalpods.rixy.service

import com.externalpods.rixy.core.model.Owner
import com.externalpods.rixy.core.network.ApiConfig
import com.externalpods.rixy.data.local.DataStoreManager
import com.externalpods.rixy.data.local.TokenManager
import com.externalpods.rixy.data.repository.FavoritesRepository
import com.externalpods.rixy.data.repository.OwnerRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: Owner) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthService(
    private val tokenManager: TokenManager,
    private val dataStoreManager: DataStoreManager,
    private val ownerRepository: OwnerRepository,
    private val favoritesRepository: FavoritesRepository
) {
    private val supabase: SupabaseClient = createSupabaseClient(
        supabaseUrl = ApiConfig.SUPABASE_URL,
        supabaseKey = ApiConfig.SUPABASE_KEY
    ) {
        install(Auth)
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun signIn(email: String, password: String): Result<Owner> {
        _authState.value = AuthState.Loading
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val token = supabase.auth.currentSessionOrNull()?.accessToken
            if (token != null) {
                tokenManager.saveToken(token)
            }
            val user = ownerRepository.getProfile()
            runCatching { favoritesRepository.syncLocalToRemote(ownerRepository) }
            dataStoreManager.saveAuthState(user.id, user.email)
            _authState.value = AuthState.Authenticated(user)
            Result.success(user)
        } catch (e: Exception) {
            val readableError = mapAuthError(e)
            _authState.value = AuthState.Error(readableError)
            Result.failure(Exception(readableError, e))
        }
    }

    suspend fun signUp(email: String, password: String): Result<Owner> {
        _authState.value = AuthState.Loading
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val token = supabase.auth.currentSessionOrNull()?.accessToken
            if (token != null) {
                tokenManager.saveToken(token)
            }
            val user = ownerRepository.getProfile()
            runCatching { favoritesRepository.syncLocalToRemote(ownerRepository) }
            dataStoreManager.saveAuthState(user.id, user.email)
            _authState.value = AuthState.Authenticated(user)
            Result.success(user)
        } catch (e: Exception) {
            val readableError = mapAuthError(e)
            _authState.value = AuthState.Error(readableError)
            Result.failure(Exception(readableError, e))
        }
    }

    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (_: Exception) {
            // Continue with local cleanup even if remote sign out fails
        }
        tokenManager.clearToken()
        dataStoreManager.clearAuthState()
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun checkSession() {
        val token = tokenManager.getToken()
        if (token == null) {
            _authState.value = AuthState.Unauthenticated
            return
        }
        _authState.value = AuthState.Loading
        try {
            val user = ownerRepository.getProfile()
            _authState.value = AuthState.Authenticated(user)
        } catch (_: Exception) {
            // Token may be expired — try refreshing
            try {
                supabase.auth.refreshCurrentSession()
                val newToken = supabase.auth.currentSessionOrNull()?.accessToken
                if (newToken != null) {
                    tokenManager.saveToken(newToken)
                    val user = ownerRepository.getProfile()
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    signOut()
                }
            } catch (_: Exception) {
                signOut()
            }
        }
    }

    fun getCurrentToken(): String? = tokenManager.getToken()

    private fun mapAuthError(error: Throwable): String {
        return when {
            error is SSLHandshakeException ||
                error.message?.contains("Chain validation failed", ignoreCase = true) == true -> {
                "No se pudo validar el certificado SSL. Verifica la fecha/hora del dispositivo y tu red."
            }
            error is UnknownHostException -> {
                "No hay conexión a internet o el servidor no está disponible."
            }
            else -> error.message ?: "Error de autenticación"
        }
    }
}

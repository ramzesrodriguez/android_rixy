package com.externalpods.rixy.core.common

/**
 * Sealed class representing API errors with specific cases.
 * Equivalent to iOS APIError enum for robust error handling.
 */
sealed class ApiError : Exception() {
    
    abstract val httpCode: Int?
    
    data class InvalidURL(
        override val message: String = "Invalid URL"
    ) : ApiError() {
        override val httpCode: Int? = null
    }
    
    data class InvalidResponse(
        override val message: String = "Invalid response from server"
    ) : ApiError() {
        override val httpCode: Int? = null
    }
    
    data class DecodingError(
        override val cause: Throwable? = null,
        override val message: String = "Failed to decode response: ${cause?.message ?: "Unknown error"}"
    ) : ApiError() {
        override val httpCode: Int? = null
    }
    
    data class Unauthorized(
        override val message: String = "Unauthorized - Please sign in again"
    ) : ApiError() {
        override val httpCode: Int = 401
    }
    
    data class Forbidden(
        override val message: String = "Forbidden - You don't have permission"
    ) : ApiError() {
        override val httpCode: Int = 403
    }
    
    data class NotFound(
        override val message: String = "Resource not found"
    ) : ApiError() {
        override val httpCode: Int = 404
    }
    
    data class ServerError(
        override val httpCode: Int = 500,
        override val message: String = "Server error ($httpCode)"
    ) : ApiError()
    
    data class HttpError(
        override val httpCode: Int,
        val serverMessage: String? = null,
        override val message: String = "HTTP Error $httpCode: ${serverMessage ?: "Unknown error"}"
    ) : ApiError()
    
    data class NetworkError(
        override val cause: Throwable,
        override val message: String = "Network error: ${cause.message ?: "Unknown error"}"
    ) : ApiError() {
        override val httpCode: Int? = null
    }
    
    data class ValidationError(
        val fieldErrors: Map<String, String> = emptyMap(),
        override val message: String = "Validation failed"
    ) : ApiError() {
        override val httpCode: Int = 422
    }
    
    data class TimeoutError(
        override val message: String = "Request timed out"
    ) : ApiError() {
        override val httpCode: Int? = null
    }
    
    data class UnknownError(
        override val cause: Throwable? = null,
        override val message: String = "Unknown error: ${cause?.message ?: ""}"
    ) : ApiError() {
        override val httpCode: Int? = null
    }

    companion object {
        /**
         * Maps HTTP status code to specific ApiError type
         */
        fun fromHttpCode(code: Int, serverMessage: String? = null): ApiError = when (code) {
            401 -> Unauthorized()
            403 -> Forbidden()
            404 -> NotFound()
            408 -> TimeoutError()
            422 -> ValidationError()
            in 500..599 -> ServerError(httpCode = code)
            else -> HttpError(httpCode = code, serverMessage = serverMessage)
        }

        /**
         * Maps Throwable to ApiError type
         */
        fun fromThrowable(throwable: Throwable): ApiError = when (throwable) {
            is ApiError -> throwable
            is java.net.SocketTimeoutException -> TimeoutError()
            is java.net.UnknownHostException -> NetworkError(throwable)
            is java.io.IOException -> NetworkError(throwable)
            else -> UnknownError(throwable)
        }
    }
}

/**
 * Extension function to convert Response to Result with proper error handling
 */
fun <T> retrofit2.Response<T>.toResult(): Result<T> {
    return if (isSuccessful) {
        body()?.let { Result.success(it) }
            ?: Result.failure(ApiError.InvalidResponse("Empty response body"))
    } else {
        val errorBody = errorBody()?.string()
        val error = ApiError.fromHttpCode(code(), errorBody)
        Result.failure(error)
    }
}

/**
 * Extension function to safely extract data from API response
 * Throws specific ApiError types instead of generic exceptions
 */
fun <T> Result<T>.getOrThrowApiError(): T {
    return getOrElse { throwable ->
        throw when (throwable) {
            is ApiError -> throwable
            else -> ApiError.fromThrowable(throwable)
        }
    }
}

/**
 * Maps an ApiError to a user-friendly message in Spanish (matching iOS)
 */
fun ApiError.toUserMessage(): String = when (this) {
    is ApiError.Unauthorized -> "Sesión expirada. Por favor inicia sesión de nuevo."
    is ApiError.Forbidden -> "No tienes permiso para realizar esta acción."
    is ApiError.NotFound -> "No se encontró el recurso solicitado."
    is ApiError.ServerError -> "Error del servidor. Intenta de nuevo más tarde."
    is ApiError.NetworkError -> "Sin conexión a internet. Verifica tu red."
    is ApiError.TimeoutError -> "La solicitud tardó demasiado. Intenta de nuevo."
    is ApiError.DecodingError -> "Error al procesar la respuesta del servidor."
    is ApiError.ValidationError -> {
        if (fieldErrors.isNotEmpty()) {
            fieldErrors.values.first()
        } else {
            "Datos inválidos. Verifica la información ingresada."
        }
    }
    is ApiError.HttpError -> serverMessage ?: "Error $httpCode"
    else -> message ?: "Error desconocido"
}

/**
 * Result wrapper for API responses that include data or error
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val error: ApiError) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    
    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): ApiError? = (this as? Error)?.error
    
    inline fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (ApiError) -> Unit): ApiResult<T> {
        if (this is Error) action(error)
        return this
    }
    
    inline fun <R> map(transform: (T) -> R): ApiResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }
    
    companion object {
        fun <T> fromResult(result: Result<T>): ApiResult<T> = result.fold(
            onSuccess = { Success(it) },
            onFailure = { Error(ApiError.fromThrowable(it)) }
        )
    }
}

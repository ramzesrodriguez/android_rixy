package com.externalpods.rixy.service

import android.content.Intent
import android.net.Uri
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Handles payment deep links and confirmation polling.
 * Equivalent to iOS PaymentHandler.
 */
class PaymentHandler(
    private val ownerRepository: OwnerRepository
) {
    /**
     * Process incoming deep link intent.
     * Returns true if the intent was handled as a payment callback.
     */
    fun handleDeepLink(intent: Intent?): PaymentResult? {
        val data = intent?.data ?: return null
        
        // Check if this is a rixy deep link
        if (data.scheme != "rixy") return null
        
        // Parse payment result
        return when (data.host) {
            "payment" -> parsePaymentResult(data)
            else -> null
        }
    }
    
    private fun parsePaymentResult(uri: Uri): PaymentResult? {
        val path = uri.path ?: return null
        
        return when {
            path.contains("/success") -> {
                val sessionId = uri.getQueryParameter("session_id")
                val type = uri.getQueryParameter("type") ?: "unknown"
                val id = uri.getQueryParameter("id") ?: ""
                PaymentResult.Success(sessionId, type, id)
            }
            path.contains("/cancel") -> {
                val type = uri.getQueryParameter("type") ?: "unknown"
                val id = uri.getQueryParameter("id") ?: ""
                PaymentResult.Cancelled(type, id)
            }
            else -> null
        }
    }
    
    /**
     * Poll for payment confirmation with exponential backoff.
     * Emits status updates until confirmed or max attempts reached.
     */
    fun pollPaymentConfirmation(
        type: String,
        id: String,
        sessionId: String? = null,
        maxAttempts: Int = 10,
        initialDelayMs: Long = 2000
    ): Flow<PaymentStatus> = flow {
        emit(PaymentStatus.Polling)
        
        var attempts = 0
        var delayMs = initialDelayMs
        
        while (attempts < maxAttempts) {
            attempts++
            
            try {
                val isConfirmed = when (type) {
                    "featured" -> checkFeaturedPaymentStatus(id)
                    "slot" -> checkSlotPaymentStatus(sessionId)
                    else -> false
                }
                
                if (isConfirmed) {
                    emit(PaymentStatus.Confirmed)
                    return@flow
                }
                
                // Exponential backoff: 2s, 4s, 8s, ... up to 30s
                delayMs = (delayMs * 1.5).toLong().coerceAtMost(30000)
                delay(delayMs)
                
            } catch (e: Exception) {
                emit(PaymentStatus.Error(e.message ?: "Unknown error"))
                delay(delayMs)
            }
        }
        
        emit(PaymentStatus.Timeout)
    }
    
    private suspend fun checkFeaturedPaymentStatus(listingId: String): Boolean {
        return try {
            // Call confirm endpoint - if successful, payment is confirmed
            ownerRepository.confirmFeaturedPayment(listingId)
            true
        } catch (e: Exception) {
            // If error is not found/pending, keep polling
            false
        }
    }
    
    private suspend fun checkSlotPaymentStatus(sessionId: String?): Boolean {
        return try {
            if (sessionId.isNullOrBlank()) return false
            ownerRepository.confirmCitySlotPayment(sessionId)
            true
        } catch (e: Exception) {
            false
        }
    }
}

sealed class PaymentResult {
    data class Success(
        val sessionId: String?,
        val type: String,
        val id: String
    ) : PaymentResult()
    
    data class Cancelled(
        val type: String,
        val id: String
    ) : PaymentResult()
}

sealed class PaymentStatus {
    data object Polling : PaymentStatus()
    data object Confirmed : PaymentStatus()
    data object Timeout : PaymentStatus()
    data class Error(val message: String) : PaymentStatus()
}

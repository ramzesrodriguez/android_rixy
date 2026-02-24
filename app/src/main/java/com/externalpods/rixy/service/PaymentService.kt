package com.externalpods.rixy.service

import com.externalpods.rixy.core.network.dto.CheckoutResponse
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Loading : PaymentState()
    data class CheckoutReady(val checkoutUrl: String, val sessionId: String?) : PaymentState()
    data object Success : PaymentState()
    data object Canceled : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentService(
    private val ownerRepository: OwnerRepository
) {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    suspend fun createFeaturedCheckout(listingId: String): Result<CheckoutResponse> {
        _paymentState.value = PaymentState.Loading
        return try {
            val checkout = ownerRepository.createFeaturedCheckout(listingId)
            val url = checkout.checkoutUrl
            if (url != null) {
                _paymentState.value = PaymentState.CheckoutReady(url, checkout.sessionId)
            }
            Result.success(checkout)
        } catch (e: Exception) {
            _paymentState.value = PaymentState.Error(e.message ?: "Checkout failed")
            Result.failure(e)
        }
    }

    fun handlePaymentSuccess() {
        _paymentState.value = PaymentState.Success
    }

    fun handlePaymentCancel() {
        _paymentState.value = PaymentState.Canceled
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}

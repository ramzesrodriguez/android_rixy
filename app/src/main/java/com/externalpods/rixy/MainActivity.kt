package com.externalpods.rixy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.externalpods.rixy.core.designsystem.theme.RixyTheme
import com.externalpods.rixy.core.network.AuthTokenRefresher
import com.externalpods.rixy.data.local.DataStoreManager
import com.externalpods.rixy.data.local.TokenManager
import com.externalpods.rixy.ui.ContentView
import com.externalpods.rixy.service.PaymentHandler
import com.externalpods.rixy.service.PaymentResult
import com.externalpods.rixy.service.PaymentStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    private val paymentHandler: PaymentHandler by inject()
    private val dataStoreManager: DataStoreManager by inject()
    private val tokenManager: TokenManager by inject()
    private val authTokenRefresher: AuthTokenRefresher by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageTag = runBlocking { dataStoreManager.appLanguage.first() }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
        enableEdgeToEdge()
        
        // Handle deep link if activity was started from one
        val initialPaymentResult = paymentHandler.handleDeepLink(intent)
        
        setContent {
            RixyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var paymentMessage by remember { mutableStateOf<String?>(null) }
                    var isProcessingPayment by remember { mutableStateOf(false) }
                    
                    // Handle payment result
                    LaunchedEffect(initialPaymentResult) {
                        initialPaymentResult?.let { result ->
                            handlePaymentResult(result) { status, message ->
                                isProcessingPayment = status
                                paymentMessage = message
                            }
                        }
                    }
                    
                    ContentView()
                    
                    // Show payment status if processing
                    paymentMessage?.let { message ->
                        // In a real app, show a Snackbar or Dialog
                        // For now, we just log it
                        android.util.Log.d("Payment", message)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refreshTokenOnAppEntry()
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links when activity is already running
        val paymentResult = paymentHandler.handleDeepLink(intent)
        paymentResult?.let { result ->
            lifecycleScope.launch {
                handlePaymentResult(result) { status, message ->
                    // Update UI with payment status
                    android.util.Log.d("Payment", "Status: $status, Message: $message")
                }
            }
        }
    }
    
    private suspend fun handlePaymentResult(
        result: PaymentResult,
        onStatusUpdate: (Boolean, String?) -> Unit
    ) {
        when (result) {
            is PaymentResult.Success -> {
                onStatusUpdate(true, "Confirmando pago...")
                
                // Start polling for confirmation
                paymentHandler.pollPaymentConfirmation(
                    type = result.type,
                    id = result.id,
                    sessionId = result.sessionId
                ).collect { status ->
                    when (status) {
                        is PaymentStatus.Polling -> {
                            onStatusUpdate(true, "Verificando pago...")
                        }
                        is PaymentStatus.Confirmed -> {
                            onStatusUpdate(false, "¡Pago confirmado exitosamente!")
                        }
                        is PaymentStatus.Timeout -> {
                            onStatusUpdate(false, "El pago está siendo procesado. Verifica más tarde.")
                        }
                        is PaymentStatus.Error -> {
                            onStatusUpdate(false, "Error al confirmar: ${status.message}")
                        }
                    }
                }
            }
            is PaymentResult.Cancelled -> {
                onStatusUpdate(false, "Pago cancelado")
            }
        }
    }

    private fun refreshTokenOnAppEntry() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (tokenManager.getToken().isNullOrBlank()) return@launch
            val refreshedToken = authTokenRefresher.refreshAccessTokenWithFallback()
            if (refreshedToken == null) {
                android.util.Log.w("Auth", "Could not refresh token on app entry")
            }
        }
    }
}

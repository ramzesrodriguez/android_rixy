package com.externalpods.rixy.feature.owner.cityslots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.*
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCitySlotsScreen(
    onBackClick: () -> Unit,
    viewModel: OwnerCitySlotsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espacios Ciudad", style = RixyTypography.H4) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RixyColors.Brand)
            }
            uiState.error != null -> EmptyErrorState(
                message = uiState.error ?: "Error",
                onRetry = { viewModel.loadSubscriptions() },
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (uiState.subscriptions.isNotEmpty()) {
                        item {
                            Text("Mis Suscripciones", style = RixyTypography.H4, color = RixyColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(uiState.subscriptions) { sub ->
                            SubscriptionCard(
                                subscription = sub,
                                onCancel = { viewModel.cancelSubscription(sub.id) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    
                    if (uiState.availableSlots.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text("Espacios Disponibles", style = RixyTypography.H4, color = RixyColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(uiState.availableSlots) { slot ->
                            AvailableSlotCard(
                                slot = slot,
                                onPurchase = { viewModel.purchaseSlot(slot) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionCard(
    subscription: com.externalpods.rixy.core.model.CitySlotSubscription,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Column(Modifier.weight(1f)) {
                    Text(subscription.slotType?.displayName ?: "Slot", style = RixyTypography.BodyMedium)
                    Text("${subscription.cityName}", style = RixyTypography.Caption, color = RixyColors.TextSecondary)
                }
                ActiveBadge(isActive = subscription.status == com.externalpods.rixy.core.model.CitySlotStatus.ACTIVE)
            }
            Spacer(Modifier.height(8.dp))
            RixyButton(
                text = "Cancelar",
                onClick = onCancel,
                variant = ButtonVariant.OUTLINE,
                size = ButtonSize.SMALL
            )
        }
    }
}

@Composable
private fun AvailableSlotCard(
    slot: com.externalpods.rixy.core.model.CitySlot,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(slot.type?.displayName ?: "Slot", style = RixyTypography.BodyMedium)
                Text("Index: ${slot.slotIndex}", style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            }
            RixyButton(
                text = "Comprar",
                onClick = onPurchase,
                size = ButtonSize.SMALL
            )
        }
    }
}

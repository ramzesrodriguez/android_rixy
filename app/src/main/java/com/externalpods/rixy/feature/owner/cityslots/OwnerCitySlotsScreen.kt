package com.externalpods.rixy.feature.owner.cityslots

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.*
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonSize
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCitySlotsScreen(
    onBackClick: () -> Unit,
    viewModel: OwnerCitySlotsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.checkoutUrl) {
        val checkoutUrl = uiState.checkoutUrl ?: return@LaunchedEffect
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure {
            viewModel.clearError()
        }
        viewModel.onCheckoutCancelled()
    }
    
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
                                onPurchase = { viewModel.openListingPickerForSlot(slot) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (uiState.showListingPicker) {
        ListingPickerSheet(
            listings = uiState.ownerListings,
            onDismiss = viewModel::dismissListingPicker,
            onSelectListing = { listing ->
                viewModel.purchaseSelectedSlot(listing.id)
            }
        )
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
            DSButton(
                title = "Cancelar",
                onClick = onCancel,
                variant = DSButtonVariant.OUTLINE,
                size = DSButtonSize.SMALL
            )
        }
    }
}

@Composable
private fun AvailableSlotCard(
    slot: AvailableSlot,
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
                Text(slot.type.displayName, style = RixyTypography.BodyMedium)
                Text("${slot.cityName} - Index: ${slot.slotIndex}", style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            }
            DSButton(
                title = "Comprar",
                onClick = onPurchase,
                size = DSButtonSize.SMALL
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListingPickerSheet(
    listings: List<Listing>,
    onDismiss: () -> Unit,
    onSelectListing: (Listing) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = RixyColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Selecciona un anuncio",
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary
            )
            Spacer(Modifier.height(12.dp))
            if (listings.isEmpty()) {
                Text(
                    text = "No tienes anuncios activos para asignar",
                    style = RixyTypography.Body,
                    color = RixyColors.TextSecondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listings, key = { it.id }) { listing ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = RixyColors.Surface),
                            onClick = { onSelectListing(listing) }
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(listing.title, style = RixyTypography.BodyMedium)
                                Text(
                                    listing.type.name,
                                    style = RixyTypography.Caption,
                                    color = RixyColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

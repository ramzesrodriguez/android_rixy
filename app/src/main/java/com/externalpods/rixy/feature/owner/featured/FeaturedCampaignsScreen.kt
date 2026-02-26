package com.externalpods.rixy.feature.owner.featured

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedCampaignsScreen(
    onBackClick: () -> Unit,
    viewModel: FeaturedCampaignsViewModel = koinViewModel()
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
        }
        viewModel.onCheckoutCancelled()
    }
    
    Scaffold(
        containerColor = RixyColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Promocionar anuncio", style = RixyTypography.H4) },
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
            uiState.error != null && uiState.filteredListings.isEmpty() -> EmptyErrorState(
                message = uiState.error ?: "Error",
                onRetry = { viewModel.loadScreenData() },
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            "Impulsa una publicación aprobada para que aparezca en la pantalla de inicio",
                            style = RixyTypography.Body,
                            color = RixyColors.TextSecondary
                        )
                        Spacer(Modifier.height(8.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Ubicación destacada") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = RixyColors.Warning.copy(alpha = 0.18f),
                                labelColor = RixyColors.Warning
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        Text(
                            text = "Activas: ${uiState.activePlacements.size} • Programadas: ${uiState.availablePlacements.size}",
                            style = RixyTypography.Body,
                            color = RixyColors.TextSecondary
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        DSSearchField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            onSearch = {},
                            placeholder = "Busca por título, tipo o categoría"
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        FeaturedTypeFilters(
                            selectedType = uiState.selectedType,
                            onTypeSelected = viewModel::onTypeSelected
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    if (uiState.filteredListings.isEmpty()) {
                        item {
                            EmptyStateView(
                                title = "No hay anuncios",
                                subtitle = "Solo anuncios aprobados pueden promocionarse",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(uiState.filteredListings, key = { it.id }) { listing ->
                            ListingPromotionCard(
                                listing = listing,
                                actionState = viewModel.actionStateFor(listing),
                                isLoading = uiState.isCreatingCheckout || uiState.actionLoadingListingIds.contains(listing.id),
                                onCheckout = { viewModel.initiateCheckout(listing) },
                                onRetry = { viewModel.retryCheckout(listing) },
                                onCancel = { viewModel.cancelCheckout(listing) },
                                onRenew = { viewModel.renewCheckout(listing) }
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
private fun FeaturedTypeFilters(
    selectedType: ListingType?,
    onTypeSelected: (ListingType?) -> Unit
) {
    val options = listOf(
        null to "Todos",
        ListingType.PRODUCT to "Producto",
        ListingType.SERVICE to "Servicio",
        ListingType.EVENT to "Evento"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (type, label) ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RixyColors.Brand.copy(alpha = 0.15f),
                    selectedLabelColor = RixyColors.Brand
                )
            )
        }
    }
}

@Composable
private fun ListingPromotionCard(
    listing: Listing,
    actionState: FeaturedListingActionState,
    isLoading: Boolean,
    onCheckout: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    onRenew: () -> Unit
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
                Text(listing.title, style = RixyTypography.BodyMedium)
                Text(listing.type.name, style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            }
            when (actionState) {
                FeaturedListingActionState.CHECKOUT -> DSButton(
                    title = "Pagar",
                    onClick = onCheckout,
                    isLoading = isLoading,
                    size = DSButtonSize.SMALL
                )
                FeaturedListingActionState.RETRY_CANCEL -> Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DSButton(
                        title = "Reintentar",
                        onClick = onRetry,
                        isLoading = isLoading,
                        size = DSButtonSize.SMALL
                    )
                    DSButton(
                        title = "Cancelar",
                        onClick = onCancel,
                        isLoading = isLoading,
                        size = DSButtonSize.SMALL
                    )
                }
                FeaturedListingActionState.RENEW -> DSButton(
                    title = "Renovar",
                    onClick = onRenew,
                    isLoading = isLoading,
                    size = DSButtonSize.SMALL
                )
                FeaturedListingActionState.ALREADY_FEATURED -> ActiveBadge(isActive = true)
            }
        }
    }
}

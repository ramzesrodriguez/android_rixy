package com.externalpods.rixy.feature.owner.featured

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
fun FeaturedCampaignsScreen(
    onBackClick: () -> Unit,
    viewModel: FeaturedCampaignsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Destacados", style = RixyTypography.H4) },
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
                onRetry = { viewModel.loadPlacements() },
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            uiState.availablePlacements.isEmpty() && uiState.activePlacements.isEmpty() -> EmptyStateView(
                title = "No hay campañas",
                subtitle = "Promociona tus anuncios para destacar",
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (uiState.activePlacements.isNotEmpty()) {
                        item {
                            Text("Activas", style = RixyTypography.H4, color = RixyColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(uiState.activePlacements) { placement ->
                            PlacementCard(
                                placement = placement,
                                isActive = true,
                                onClick = { /* View details */ }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    
                    if (uiState.availablePlacements.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text("Disponibles", style = RixyTypography.H4, color = RixyColors.TextPrimary)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(uiState.availablePlacements) { placement ->
                            AvailablePlacementCard(
                                placement = placement,
                                onPurchase = { viewModel.initiateCheckout(placement) }
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
private fun PlacementCard(
    placement: com.externalpods.rixy.core.model.FeaturedPlacement,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(placement.listingTitle ?: "Anuncio", style = RixyTypography.BodyMedium)
            Text("$${placement.amountCents?.div(100)} ${placement.currency}", style = RixyTypography.Price, color = RixyColors.Brand)
            if (isActive) {
                ActiveBadge(isActive = true)
            }
        }
    }
}

@Composable
private fun AvailablePlacementCard(
    placement: com.externalpods.rixy.core.model.FeaturedPlacement,
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
                Text(placement.slotType?.displayName ?: "Espacio", style = RixyTypography.BodyMedium)
                Text("$${placement.basePriceCents?.div(100)} ${placement.currency}", style = RixyTypography.Price, color = RixyColors.Brand)
            }
            RixyButton(
                text = "Comprar",
                onClick = onPurchase,
                size = ButtonSize.SMALL
            )
        }
    }
}

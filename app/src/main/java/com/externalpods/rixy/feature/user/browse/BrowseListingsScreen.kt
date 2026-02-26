package com.externalpods.rixy.feature.user.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.DSListingCard
import com.externalpods.rixy.core.designsystem.components.DSListingCardSkeleton
import com.externalpods.rixy.core.designsystem.components.DSMainHeader
import com.externalpods.rixy.core.designsystem.components.DSSearchField
import com.externalpods.rixy.core.designsystem.components.DSTopBar
import com.externalpods.rixy.core.designsystem.components.EmptyStateSearch
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BrowseListingsScreen(
    citySlug: String? = null,
    onBackClick: (() -> Unit)?,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrowseListingsViewModel = koinViewModel { 
        parametersOf(citySlug)
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val latestUiState by rememberUpdatedState(uiState)
    val gridState = rememberLazyGridState()
    
    // Infinite scroll
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = latestUiState.listings.size
                if (lastVisibleItem >= totalItems - 5 &&
                    !latestUiState.isLoading &&
                    !latestUiState.isLoadingMore &&
                    latestUiState.hasMorePages
                ) {
                    viewModel.loadNextPage()
                }
            }
    }
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (onBackClick != null) {
                DSTopBar(
                    title = "Explorar",
                    onBackClick = onBackClick,
                    backgroundColor = RixyColors.Background,
                    titleStyle = RixyTypography.H1
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (onBackClick == null) {
                DSMainHeader(title = "Explorar")
            }

            // Search bar
            DSSearchField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                onSearch = { viewModel.search() },
                placeholder = "Buscar anuncios...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            // Type filter chips
            TypeFilterChips(
                selectedType = uiState.selectedType,
                onTypeSelected = viewModel::onTypeSelected,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content
            when {
                uiState.isLoading && uiState.listings.isEmpty() -> {
                    BrowseLoadingState()
                }
                uiState.error != null && uiState.listings.isEmpty() -> {
                    ErrorViewGeneric(
                        message = uiState.error ?: "Error al cargar",
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.listings.isEmpty() && uiState.searchQuery.isNotEmpty() -> {
                    EmptyStateSearch(
                        query = uiState.searchQuery,
                        onClearSearch = { viewModel.onSearchQueryChange("") }
                    )
                }
                uiState.listings.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay anuncios disponibles",
                            style = RixyTypography.H4,
                            color = RixyColors.TextSecondary
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.listings,
                            key = { it.id }
                        ) { listing ->
                            DSListingCard(
                                title = listing.title,
                                imageUrl = listing.photoUrls?.firstOrNull(),
                                priceFormatted = listing.productDetails?.priceAmount
                                    ?: listing.serviceDetails?.priceAmount
                                    ?: listing.eventDetails?.priceAmount,
                                type = com.externalpods.rixy.core.designsystem.components.ListingType.valueOf(listing.type.name),
                                businessName = listing.business?.name,
                                isFavorite = uiState.favoriteIds.contains(listing.id),
                                onFavoriteClick = if (uiState.loadingFavoriteIds.contains(listing.id)) {
                                    null
                                } else {
                                    { viewModel.toggleFavorite(listing.id) }
                                },
                                onCardClick = { onListingClick(listing) },
                                useFixedWidth = false
                            )
                        }
                        
                        // Loading indicator at bottom
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = RixyColors.Brand
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeFilterChips(
    selectedType: ListingType?,
    onTypeSelected: (ListingType?) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = listOf(
        null to "Todos",
        ListingType.PRODUCT to "Productos",
        ListingType.SERVICE to "Servicios",
        ListingType.EVENT to "Eventos"
    )
    
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(types.size) { index ->
            val (type, label) = types[index]
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(label, style = RixyTypography.Body) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RixyColors.Brand.copy(alpha = 0.15f),
                    selectedLabelColor = RixyColors.Brand
                )
            )
        }
    }
}

@Composable
private fun BrowseLoadingState(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            DSListingCardSkeleton(modifier = Modifier.fillMaxWidth())
        }
    }
}

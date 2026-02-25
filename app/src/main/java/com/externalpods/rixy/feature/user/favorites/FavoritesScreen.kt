package com.externalpods.rixy.feature.user.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.common.CurrencyFormatter
import com.externalpods.rixy.core.designsystem.components.DSListingCard
import com.externalpods.rixy.core.designsystem.components.DSListingCardSkeleton
import com.externalpods.rixy.core.designsystem.components.DSSearchField
import com.externalpods.rixy.core.designsystem.components.EmptyStateFavorites
import com.externalpods.rixy.core.designsystem.components.EmptyStateSearch
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onListingClick: (Listing) -> Unit,
    onBackClick: (() -> Unit)?
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = RixyColors.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Favoritos",
                style = RixyTypography.H1,
                color = RixyColors.TextPrimary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            DSSearchField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                onSearch = { },
                placeholder = "Buscar favoritos...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            FavoritesTypeFilters(
                selectedType = uiState.selectedType,
                onTypeSelected = viewModel::onTypeSelected,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            when {
                uiState.isLoading -> {
                    FavoritesLoadingGrid()
                }

                uiState.error != null -> {
                    ErrorViewGeneric(
                        message = uiState.error ?: "Error al cargar favoritos",
                        onRetry = viewModel::loadFavorites,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.favorites.isEmpty() -> {
                    EmptyStateFavorites(
                        onBrowseClick = { },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp)
                    )
                }

                uiState.filteredFavorites.isEmpty() -> {
                    EmptyStateSearch(
                        query = uiState.searchQuery,
                        onClearSearch = viewModel::clearFilters
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.filteredFavorites,
                            key = { it.id }
                        ) { listing ->
                            val isLoadingFavorite = uiState.loadingFavoriteIds.contains(listing.id)
                            DSListingCard(
                                title = listing.title,
                                imageUrl = listing.photoUrls?.firstOrNull(),
                                priceFormatted = remember(listing) { listing.formatPrice() },
                                type = com.externalpods.rixy.core.designsystem.components.ListingType.valueOf(
                                    listing.type.name
                                ),
                                businessName = listing.business?.name,
                                isFavorite = uiState.favoriteIds.contains(listing.id),
                                onFavoriteClick = if (isLoadingFavorite) null else {
                                    { viewModel.toggleFavorite(listing.id) }
                                },
                                onCardClick = { onListingClick(listing) },
                                useFixedWidth = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesTypeFilters(
    selectedType: ListingType?,
    onTypeSelected: (ListingType?) -> Unit,
    modifier: Modifier = Modifier
) {
    val chips = listOf(
        ListingType.PRODUCT,
        ListingType.SERVICE,
        ListingType.EVENT
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips.size) { index ->
            val type = chips[index]
            FilterChip(
                selected = selectedType == type,
                onClick = {
                    onTypeSelected(if (selectedType == type) null else type)
                },
                label = { Text(type.displayName, style = RixyTypography.Body) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RixyColors.Brand.copy(alpha = 0.15f),
                    selectedLabelColor = RixyColors.Brand
                )
            )
        }
    }
}

@Composable
private fun FavoritesLoadingGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) {
            DSListingCardSkeleton(modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun Listing.formatPrice(): String {
    val amount = productDetails?.priceAmount ?: serviceDetails?.priceAmount ?: eventDetails?.priceAmount
    val currency = productDetails?.currency ?: serviceDetails?.currency ?: eventDetails?.currency ?: "MXN"
    return CurrencyFormatter.format(amount, currency)
}

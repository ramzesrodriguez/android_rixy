package com.externalpods.rixy.feature.user.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onListingClick: (Listing) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos", style = RixyTypography.H4) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = {},
                placeholder = "Buscar en favoritos...",
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Type filter
            TypeFilterChips(
                selectedType = uiState.selectedType,
                onTypeSelected = viewModel::onTypeSelected
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RixyColors.Brand)
                }
                uiState.error != null -> EmptyErrorState(
                    message = uiState.error ?: "Error",
                    onRetry = viewModel::loadFavorites,
                    modifier = Modifier.fillMaxSize()
                )
                uiState.filteredFavorites.isEmpty() -> EmptyFavoritesState()
                else -> FavoritesGrid(
                    favorites = uiState.filteredFavorites,
                    onListingClick = onListingClick,
                    onRemove = viewModel::removeFromFavorites
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeFilterChips(
    selectedType: ListingType?,
    onTypeSelected: (ListingType?) -> Unit
) {
    val types = listOf(
        null to "Todos",
        ListingType.PRODUCT to "Productos",
        ListingType.SERVICE to "Servicios",
        ListingType.EVENT to "Eventos"
    )
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        types.forEach { (type, label) ->
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
private fun FavoritesGrid(
    favorites: List<Listing>,
    onListingClick: (Listing) -> Unit,
    onRemove: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(favorites, key = { it.id }) { listing ->
            Box {
                ListingCard(
                    title = listing.title,
                    imageUrl = listing.photoUrls?.firstOrNull(),
                    price = listing.productDetails?.priceAmount,
                    type = listing.type,
                    businessName = listing.business?.name,
                    onClick = { onListingClick(listing) }
                )
                // Remove button
                IconButton(
                    onClick = { onRemove(listing.id) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = RixyColors.Error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "♥",
                style = RixyTypography.H1,
                color = RixyColors.TextTertiary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tienes favoritos",
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary
            )
            Text(
                text = "Guarda tus anuncios favoritos para verlos aquí",
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary
            )
        }
    }
}

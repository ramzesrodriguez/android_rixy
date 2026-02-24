package com.externalpods.rixy.feature.user.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.v2.DSListingCardCompact
import com.externalpods.rixy.core.designsystem.components.v2.DSListingCardCompactSkeleton
import com.externalpods.rixy.core.designsystem.components.v2.EmptyStateFavorites
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import org.koin.androidx.compose.koinViewModel

/**
 * FavoritesScreen - User favorites screen (mirrors iOS FavoritesView)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onListingClick: (Listing) -> Unit,
    onBackClick: (() -> Unit)?
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos", style = RixyTypography.Title3) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RixyColors.Surface,
                    titleContentColor = RixyColors.TextPrimary,
                    navigationIconContentColor = RixyColors.TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Loading state
                FavoritesLoading()
            } else if (favorites.isEmpty()) {
                EmptyStateFavorites(
                    onBrowseClick = { /* Navigate to browse */ }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favorites) { listing ->
                        DSListingCardCompact(
                            title = listing.title,
                            imageUrl = listing.photoUrls?.firstOrNull(),
                            priceFormatted = listing.productDetails?.priceAmount,
                            type = com.externalpods.rixy.core.designsystem.components.v2.ListingType.valueOf(
                                listing.type.name
                            ),
                            businessName = listing.business?.name,
                            onClick = { onListingClick(listing) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesLoading() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            DSListingCardCompactSkeleton()
        }
    }
}

package com.externalpods.rixy.feature.user.cityhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.CityCardSkeleton
import com.externalpods.rixy.core.designsystem.components.EmptyErrorState
import com.externalpods.rixy.core.designsystem.components.ListingCard
import com.externalpods.rixy.core.designsystem.components.ListingCardSkeleton
import com.externalpods.rixy.core.designsystem.components.SectionHeader
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.CityHomeSection
import com.externalpods.rixy.core.model.Listing
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityHomeScreen(
    city: City,
    onListingClick: (Listing) -> Unit,
    onSeeAllListings: () -> Unit,
    viewModel: CityHomeViewModel = koinViewModel { parametersOf(city.id, city.slug) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = city.name,
                        style = RixyTypography.H4,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = onSeeAllListings) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Ver todos"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.sections.isEmpty() -> {
                CityHomeLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.error != null && uiState.sections.isEmpty() -> {
                EmptyErrorState(
                    message = uiState.error ?: "Error al cargar",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                CityHomeContent(
                    sections = uiState.sections,
                    onListingClick = onListingClick,
                    modifier = Modifier.padding(paddingValues),
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}

@Composable
private fun CityHomeContent(
    sections: List<CityHomeSection>,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        sections.forEach { section ->
            item(key = section.id) {
                SectionHeader(
                    title = section.title,
                    onSeeAllClick = null, // TODO: Navigate to see all
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (section.displayType) {
                    "horizontal" -> {
                        ListingsHorizontalRow(
                            listings = section.listings,
                            onListingClick = onListingClick
                        )
                    }
                    "grid" -> {
                        ListingsGrid(
                            listings = section.listings,
                            onListingClick = onListingClick
                        )
                    }
                    else -> {
                        ListingsHorizontalRow(
                            listings = section.listings,
                            onListingClick = onListingClick
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RixyColors.Brand)
                }
            }
        }
    }
}

@Composable
private fun ListingsHorizontalRow(
    listings: List<Listing>,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listings, key = { it.id }) { listing ->
            ListingCard(
                title = listing.title,
                imageUrl = listing.photoUrls?.firstOrNull(),
                price = listing.productDetails?.priceAmount,
                type = listing.type,
                businessName = listing.business?.name,
                onClick = { onListingClick(listing) },
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
private fun ListingsGrid(
    listings: List<Listing>,
    onListingClick: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listings.forEach { listing ->
            ListingCard(
                title = listing.title,
                imageUrl = listing.photoUrls?.firstOrNull(),
                price = listing.productDetails?.priceAmount,
                type = listing.type,
                businessName = listing.business?.name,
                onClick = { onListingClick(listing) }
            )
        }
    }
}

@Composable
private fun CityHomeLoadingState(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(3) {
            // Section header skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
                    .padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Horizontal scroll skeleton
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(3) {
                    ListingCardSkeleton(
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

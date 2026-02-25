package com.externalpods.rixy.feature.user.listingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonSize
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.components.DSCard
import com.externalpods.rixy.core.designsystem.components.DSErrorView
import com.externalpods.rixy.core.designsystem.components.DSSkeleton
import com.externalpods.rixy.core.designsystem.components.DSTypeBadge
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.components.ListingType
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType as ModelListingType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * ListingDetailScreenV2 - iOS-style Listing Detail Screen
 *
 * Replicates iOS ListingDetailView with:
 * - Full-width image gallery
 * - Floating action buttons (back, favorite, share)
 * - Title and price section
 * - Business info card
 * - Description section
 * - Contact CTA at bottom
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String,
    citySlug: String,
    onBackClick: () -> Unit,
    onBusinessClick: (String) -> Unit,
    onShareClick: () -> Unit,
    viewModel: ListingDetailViewModel = koinViewModel {
        parametersOf(citySlug, listingId)
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite)
                                Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite)
                                RixyColors.Error else RixyColors.TextPrimary
                        )
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                ListingDetailLoadingState(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.error != null -> {
                ErrorViewGeneric(
                    message = uiState.error ?: "Error al cargar",
                    onRetry = { viewModel.loadListing() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.listing != null -> {
                ListingDetailContent(
                    listing = uiState.listing!!,
                    onBusinessClick = onBusinessClick,
                    onContactClick = { viewModel.onContactClick() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                ErrorViewGeneric(
                    message = "No se pudo cargar el anuncio",
                    onRetry = { viewModel.loadListing() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ListingDetailContent(
    listing: Listing,
    onBusinessClick: (String) -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Image Gallery
        item {
            ImageGallery(
                images = listing.photoUrls ?: emptyList(),
                type = mapListingType(listing.type)
            )
        }

        // Title and Price Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Type badge
                DSTypeBadge(type = mapListingType(listing.type))

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = listing.title,
                    style = RixyTypography.H2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                val price = formatPrice(listing)
                if (price != null) {
                    Text(
                        text = price,
                        style = RixyTypography.H3.copy(
                            color = RixyColors.Brand
                        )
                    )
                }
            }
        }

        // Business Card
        listing.business?.let { business ->
            item {
                DSCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { onBusinessClick(business.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Business logo placeholder
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RixyColors.SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = business.name.firstOrNull()?.uppercase() ?: "?",
                                style = RixyTypography.H4
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = business.name,
                                style = RixyTypography.BodyMedium
                            )
                            Text(
                                text = "Ver perfil →",
                                style = RixyTypography.Caption,
                                color = RixyColors.Brand
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Description Section
        item {
            listing.description?.let { description ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Descripción",
                        style = RixyTypography.H4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        style = RixyTypography.Body,
                        color = RixyColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Details Section
        item {
            DSCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    // Location
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = RixyColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ubicación del vendedor",
                            style = RixyTypography.Body,
                            color = RixyColors.TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Availability
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = RixyColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Disponible ahora",
                            style = RixyTypography.Body,
                            color = RixyColors.Success
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Contact CTA
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DSButton(
                    title = "Contactar",
                    onClick = onContactClick,
                    variant = DSButtonVariant.PRIMARY,
                    size = DSButtonSize.LARGE,
                    fillWidth = true
                )
            }
        }
    }
}

@Composable
private fun ImageGallery(
    images: List<String>,
    type: ListingType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        if (images.isNotEmpty()) {
            AsyncImage(
                model = images.first(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RixyColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📷",
                    style = RixyTypography.H1
                )
            }
        }

        // Type badge overlay
        DSTypeBadge(
            type = type,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )
    }
}

@Composable
private fun ListingDetailLoadingState(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Image skeleton
        item {
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        // Content skeleton
        items(5) {
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Helper functions
private fun mapListingType(type: ModelListingType): ListingType {
    return when (type) {
        ModelListingType.PRODUCT -> ListingType.PRODUCT
        ModelListingType.SERVICE -> ListingType.SERVICE
        ModelListingType.EVENT -> ListingType.EVENT
        else -> ListingType.PRODUCT
    }
}

private fun formatPrice(listing: Listing): String? {
    return listing.productDetails?.priceAmount?.let { "$ $it" }
}

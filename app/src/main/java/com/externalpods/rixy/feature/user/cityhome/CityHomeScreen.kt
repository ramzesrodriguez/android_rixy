package com.externalpods.rixy.feature.user.cityhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonSize
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.components.DSCard
import com.externalpods.rixy.core.designsystem.components.DSCategoryCard
import com.externalpods.rixy.core.designsystem.components.DSCityHeroSection
import com.externalpods.rixy.core.designsystem.components.DSHeroSlotCard
import com.externalpods.rixy.core.designsystem.components.DSListingCardCompact
import com.externalpods.rixy.core.designsystem.components.DSOutlineButton
import com.externalpods.rixy.core.designsystem.components.DSSectionHeader
import com.externalpods.rixy.core.designsystem.components.ListingType
import com.externalpods.rixy.core.designsystem.components.DSCityHeroSkeleton
import com.externalpods.rixy.core.designsystem.components.DSCategoryCardSkeleton
import com.externalpods.rixy.core.designsystem.components.DSListingCardSkeleton
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.Listing
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.NumberFormat
import java.util.Locale

/**
 * CityHomeScreen - iOS-style City Home Screen
 * 
 * Replicates CityHomeView.swift from iOS with:
 * - CityHeroSection with gradient and stats
 * - CategoryGrid (2x2) with emoji icons
 * - Featured/Slot sections with horizontal scroll
 * - Recent feed with compact cards
 * - Business CTA section
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityHomeScreen(
    city: City,
    onListingClick: (Listing) -> Unit,
    onSeeAllListings: () -> Unit,
    onChangeCity: () -> Unit,
    onBusinessCTAClick: () -> Unit,
    viewModel: CityHomeViewModel = koinViewModel { parametersOf(city.id, city.slug) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        text = uiState.city?.name ?: city.name,
                        style = RixyTypography.H4,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    DSOutlineButton(
                        title = "Cambiar",
                        onClick = onChangeCity,
                        size = DSButtonSize.SMALL
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        }
    ) { paddingValues ->
        CityHomeContent(
            city = uiState.city ?: city,
            featured = uiState.featured,
            feed = uiState.feed,
            isLoading = uiState.isLoading,
            onListingClick = onListingClick,
            onSeeAllListings = onSeeAllListings,
            onBusinessCTAClick = onBusinessCTAClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CityHomeContent(
    city: City,
    featured: Listing?,
    feed: List<Listing>,
    isLoading: Boolean,
    onListingClick: (Listing) -> Unit,
    onSeeAllListings: () -> Unit,
    onBusinessCTAClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        if (isLoading) {
            // Loading State
            item { DSCityHeroSkeleton(modifier = Modifier.padding(16.dp)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // Category skeletons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) {
                        DSCategoryCardSkeleton(modifier = Modifier.weight(1f))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            
            // Listing skeletons
            items(3) {
                DSListingCardSkeleton(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            return@LazyColumn
        }
        
        // Hero Section with Gradient
        item {
            DSCityHeroSection(
                cityName = city.name,
                location = listOfNotNull(city.state, city.country).joinToString(", ").ifEmpty { null },
                businessCount = city.resolvedBusinessCount,
                listingCount = city.resolvedListingCount,
                subscriptionCount = city.resolvedSubscriptionCount
            )
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
        
        // Category Grid (2x2 with emojis - matching iOS)
        item {
            CategoryGrid(
                onCategoryClick = { type ->
                    // Navigate to browse with type filter
                }
            )
        }
        
        item { Spacer(modifier = Modifier.height(32.dp)) }
        
        // Featured Section
        if (featured != null) {
            item {
                DSSectionHeader(
                    title = "Destacado",
                    onActionClick = onSeeAllListings
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                DSHeroSlotCard(
                    title = featured.title,
                    imageUrl = featured.photoUrls?.firstOrNull(),
                    priceFormatted = featured.productDetails?.priceAmount,
                    type = ListingType.valueOf(featured.type.name),
                    businessName = featured.business?.name,
                    onClick = { onListingClick(featured) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // Recent Feed Section
        if (feed.isNotEmpty()) {
            item {
                DSSectionHeader(
                    title = "Recientes",
                    onActionClick = onSeeAllListings
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            items(
                items = feed.take(6),
                key = { it.id }
            ) { listing ->
                DSListingCardCompact(
                    title = listing.title,
                    imageUrl = listing.photoUrls?.firstOrNull(),
                    priceFormatted = listing.productDetails?.priceAmount,
                    type = ListingType.valueOf(listing.type.name),
                    businessName = listing.business?.name,
                    onClick = { onListingClick(listing) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        // Business CTA Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            
            DSCard(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Tienes un negocio?",
                        style = RixyTypography.H4
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Publica tus productos, servicios o eventos en ${city.name}",
                        style = RixyTypography.Body,
                        color = RixyColors.TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DSButton(
                        title = "Registrar mi negocio",
                        onClick = onBusinessCTAClick,
                        variant = DSButtonVariant.PRIMARY,
                        size = DSButtonSize.LARGE,
                        icon = Icons.Default.AddBusiness
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CategoryGrid(
    onCategoryClick: (ListingType) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DSCategoryCard(
                title = "Productos",
                emoji = "🛍️",
                color = RixyColors.Product,
                onClick = { onCategoryClick(ListingType.PRODUCT) },
                modifier = Modifier.weight(1f)
            )
            DSCategoryCard(
                title = "Servicios",
                emoji = "🔧",
                color = RixyColors.Service,
                onClick = { onCategoryClick(ListingType.SERVICE) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DSCategoryCard(
                title = "Eventos",
                emoji = "🎉",
                color = RixyColors.Event,
                onClick = { onCategoryClick(ListingType.EVENT) },
                modifier = Modifier.weight(1f)
            )
            DSCategoryCard(
                title = "Negocios",
                emoji = "🏪",
                color = RixyColors.Community,
                onClick = { /* Navigate to businesses */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun formatNumber(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}

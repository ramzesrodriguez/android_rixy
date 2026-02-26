package com.externalpods.rixy.feature.user.cityhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.externalpods.rixy.core.designsystem.components.DSListingCard
import com.externalpods.rixy.core.designsystem.components.DSAsyncImage
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import com.externalpods.rixy.core.model.CitySection
import com.externalpods.rixy.core.model.CitySlotType
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType as ModelListingType
import com.externalpods.rixy.core.model.PublicCitySlot
import com.externalpods.rixy.core.model.SlotListing
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar (replacing Scaffold topBar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.city?.name ?: city.name,
                style = RixyTypography.H1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            DSOutlineButton(
                title = "Cambiar",
                onClick = onChangeCity,
                size = DSButtonSize.SMALL,
                icon = Icons.Default.LocationOn
            )
        }
        
        CityHomeContent(
            city = uiState.city ?: city,
            featured = uiState.featured,
            feed = uiState.feed,
            sections = uiState.sections,
            sectionItems = uiState.sectionItems,
            slots = uiState.slots,
            isLoading = uiState.isLoading,
            onListingClick = onListingClick,
            onSeeAllListings = onSeeAllListings,
            onBusinessCTAClick = onBusinessCTAClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CityHomeContent(
    city: City,
    featured: Listing?,
    feed: List<Listing>,
    sections: List<CitySection>,
    sectionItems: Map<String, List<Listing>>,
    slots: List<PublicCitySlot>,
    isLoading: Boolean,
    onListingClick: (Listing) -> Unit,
    onSeeAllListings: () -> Unit,
    onBusinessCTAClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
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

        // Slot-based Hero Listings (matching iOS HOME_HERO_SPOTLIGHT)
        val heroSlots = slots.filter {
            it.slotType == CitySlotType.HOME_HERO_SPOTLIGHT && it.hasContent
        }
        if (heroSlots.isNotEmpty()) {
            item {
                DSSectionHeader(
                    title = "Destacados",
                    onActionClick = if (heroSlots.size > 1) onSeeAllListings else null
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (heroSlots.size == 1) {
                    val slotListing = heroSlots.first().listing
                    if (slotListing != null) {
                        DSHeroSlotCard(
                            title = slotListing.title,
                            imageUrl = slotListing.photoUrls.firstOrNull(),
                            priceFormatted = formatSlotPrice(slotListing),
                            type = ListingType.valueOf(slotListing.type.name),
                            businessName = slotListing.business?.name,
                            onClick = {
                                onListingClick(slotListing.toListing())
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = heroSlots,
                            key = { it.id }
                        ) { slot ->
                            slot.listing?.let { slotListing ->
                                DSListingCard(
                                    title = slotListing.title,
                                    imageUrl = slotListing.photoUrls.firstOrNull(),
                                    priceFormatted = formatSlotPrice(slotListing),
                                    type = ListingType.valueOf(slotListing.type.name),
                                    businessName = slotListing.business?.name,
                                    onCardClick = { onListingClick(slotListing.toListing()) }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

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

        // Banner slots (matching iOS HOME_HORIZONTAL_CAROUSEL)
        val carouselSlots = slots.filter {
            it.slotType == CitySlotType.HOME_HORIZONTAL_CAROUSEL && it.hasContent
        }
        if (carouselSlots.isNotEmpty()) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = carouselSlots,
                        key = { it.id }
                    ) { slot ->
                        slot.listing?.let { slotListing ->
                            BannerSlotCard(
                                listing = slotListing,
                                onClick = { onListingClick(slotListing.toListing()) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Dynamic City Sections
        sections
            .filter { it.isVisible }
            .sortedBy { it.order }
            .forEach { section ->
                val itemsForSection = sectionItems[section.id].orEmpty()
                if (itemsForSection.isNotEmpty()) {
                    item(key = section.id) {
                        DSSectionHeader(
                            title = section.title,
                            onActionClick = onSeeAllListings
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = itemsForSection,
                                key = { it.id }
                            ) { listing ->
                                DSListingCard(
                                    title = listing.title,
                                    imageUrl = listing.photoUrls?.firstOrNull(),
                                    priceFormatted = formatListingPrice(listing),
                                    type = ListingType.valueOf(listing.type.name),
                                    businessName = listing.business?.name,
                                    onCardClick = { onListingClick(listing) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
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
                    priceFormatted = formatListingPrice(listing),
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
private fun BannerSlotCard(
    listing: SlotListing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(320.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(RixyColors.Surface)
            .clickable(onClick = onClick)
    ) {
        DSAsyncImage(
            imageUrl = listing.photoUrls.firstOrNull(),
            contentDescription = listing.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .padding(0.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.55f)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = listing.title,
                    style = RixyTypography.BodyMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun SlotListing.toListing(): Listing {
    return Listing(
        id = id,
        type = type,
        title = title,
        description = description,
        categoryTag = categoryTag,
        photoUrls = photoUrls,
        business = business?.let {
            com.externalpods.rixy.core.model.BusinessSummary(
                id = it.id,
                name = it.name,
                logoUrl = it.logoUrl
            )
        },
        productDetails = if (type == ModelListingType.PRODUCT) {
            com.externalpods.rixy.core.model.ProductDetails(
                priceAmount = priceAmount,
                currency = currency,
                priceType = priceType
            )
        } else null,
        serviceDetails = if (type == ModelListingType.SERVICE) {
            com.externalpods.rixy.core.model.ServiceDetails(
                priceAmount = priceAmount,
                currency = currency
            )
        } else null,
        eventDetails = if (type == ModelListingType.EVENT) {
            com.externalpods.rixy.core.model.EventDetails(
                priceAmount = priceAmount,
                currency = currency
            )
        } else null
    )
}

private fun formatListingPrice(listing: Listing): String? {
    val productPrice = listing.productDetails?.priceAmount
    val productCurrency = listing.productDetails?.currency
    val servicePrice = listing.serviceDetails?.priceAmount
    val serviceCurrency = listing.serviceDetails?.currency
    val eventPrice = listing.eventDetails?.priceAmount
    val eventCurrency = listing.eventDetails?.currency

    return when {
        !productPrice.isNullOrBlank() -> "${productCurrency ?: "MXN"} $$productPrice"
        !servicePrice.isNullOrBlank() -> "${serviceCurrency ?: "MXN"} $$servicePrice"
        !eventPrice.isNullOrBlank() -> "${eventCurrency ?: "MXN"} $$eventPrice"
        else -> null
    }
}

private fun formatSlotPrice(listing: SlotListing): String? {
    if (listing.priceAmount.isNullOrBlank()) return null
    return "${listing.currency ?: "MXN"} $${listing.priceAmount}"
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

package com.externalpods.rixy.feature.user.businessprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.components.DSCard
import com.externalpods.rixy.core.designsystem.components.DSListingCard
import com.externalpods.rixy.core.designsystem.components.DSSectionHeader
import com.externalpods.rixy.core.designsystem.components.resolveRemoteImageUrl
import com.externalpods.rixy.core.designsystem.components.DSSkeleton
import com.externalpods.rixy.core.designsystem.components.EmptyStateNoListings
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.components.ListingType
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType as ModelListingType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * BusinessProfileScreen - iOS-style Business Profile Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    citySlug: String,
    businessId: String,
    onBackClick: () -> Unit,
    onListingClick: (Listing) -> Unit,
    onPhoneClick: (String) -> Unit,
    viewModel: BusinessProfileViewModel = koinViewModel {
        parametersOf(citySlug, businessId)
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("Perfil del Negocio") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                BusinessProfileLoadingState(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.error != null -> {
                ErrorViewGeneric(
                    message = uiState.error ?: "Error al cargar",
                    onRetry = { /* retry */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.business != null -> {
                BusinessProfileContent(
                    business = uiState.business!!,
                    listings = uiState.listings,
                    onListingClick = onListingClick,
                    onPhoneClick = onPhoneClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun BusinessProfileContent(
    business: Business,
    listings: List<Listing>,
    onListingClick: (Listing) -> Unit,
    onPhoneClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Section with Cover and Logo
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Cover Image
                AsyncImage(
                    model = resolveRemoteImageUrl(business.headerImageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
                
                // Business Logo (positioned at bottom)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RixyColors.Surface)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (business.logoUrl != null) {
                            AsyncImage(
                                model = resolveRemoteImageUrl(business.logoUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = business.name.firstOrNull()?.uppercase() ?: "?",
                                style = RixyTypography.H2
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Name
                    Text(
                        text = business.name,
                        style = RixyTypography.H3.copy(
                            color = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Action Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Call button
                business.phone?.let { phone ->
                    DSButton(
                        title = "Llamar",
                        onClick = { onPhoneClick(phone) },
                        variant = DSButtonVariant.PRIMARY,
                        icon = Icons.Default.Phone,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // About Section
        business.description?.let { description ->
            item {
                DSCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "Acerca de",
                            style = RixyTypography.H4
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = description,
                            style = RixyTypography.Body,
                            color = RixyColors.TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        // Contact Information
        item {
            DSCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Información de contacto",
                        style = RixyTypography.H4
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Location
                    business.addressText?.let { address ->
                        ContactInfoRow(
                            icon = Icons.Outlined.LocationOn,
                            text = address
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Phone
                    business.phone?.let { phone ->
                        ContactInfoRow(
                            icon = Icons.Default.Phone,
                            text = phone
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Business Hours (if available)
        item {
            DSCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
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
                            text = "Horario",
                            style = RixyTypography.BodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Lunes a Viernes: 9:00 AM - 6:00 PM",
                        style = RixyTypography.Body,
                        color = RixyColors.TextSecondary
                    )
                    Text(
                        text = "Sábados: 10:00 AM - 2:00 PM",
                        style = RixyTypography.Body,
                        color = RixyColors.TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Listings Section
        item {
            if (listings.isNotEmpty()) {
                DSSectionHeader(
                    title = "Publicaciones",
                    onActionClick = null,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listings) { listing ->
                        DSListingCard(
                            title = listing.title,
                            imageUrl = listing.photoUrls?.firstOrNull(),
                            priceFormatted = formatPrice(listing),
                            type = mapListingType(listing.type),
                            businessName = null,
                            onCardClick = { onListingClick(listing) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                EmptyStateNoListings(
                    onCreateListing = { /* Navigate to create */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RixyColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
    }
}

@Composable
private fun BusinessProfileLoadingState(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Cover skeleton
        item {
            DSSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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

package com.externalpods.rixy.feature.user.businessprofile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.components.EmptyErrorState
import com.externalpods.rixy.core.designsystem.components.ListingCard
import com.externalpods.rixy.core.designsystem.components.ButtonVariant
import com.externalpods.rixy.core.designsystem.components.RixyButton
import com.externalpods.rixy.core.designsystem.components.SectionHeader
import com.externalpods.rixy.core.designsystem.components.SkeletonBox
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    businessId: String,
    onBackClick: () -> Unit,
    onListingClick: (Listing) -> Unit,
    viewModel: BusinessProfileViewModel = koinViewModel { parametersOf(businessId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.business?.name ?: "Perfil de negocio",
                        style = RixyTypography.H4,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                BusinessProfileLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.error != null -> {
                EmptyErrorState(
                    message = uiState.error ?: "Error al cargar",
                    onRetry = { /* Retry */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.business != null -> {
                BusinessProfileContent(
                    uiState = uiState,
                    onListingClick = onListingClick,
                    onWhatsAppClick = { viewModel.onWhatsAppClick() },
                    onPhoneClick = { viewModel.onPhoneClick() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun BusinessProfileContent(
    uiState: BusinessProfileUiState,
    onListingClick: (Listing) -> Unit,
    onWhatsAppClick: () -> Unit,
    onPhoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val business = uiState.business!!
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header with logo and header image
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Header image
                AsyncImage(
                    model = business.headerImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
                
                // Logo overlay
                business.logoUrl?.let { logo ->
                    AsyncImage(
                        model = logo,
                        contentDescription = business.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RixyColors.Surface)
                            .align(Alignment.BottomStart)
                            .padding(4.dp)
                    )
                }
            }
        }
        
        // Business info
        item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = business.name ?: "",
                    style = RixyTypography.H3,
                    color = RixyColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                business.description?.let { desc ->
                    Text(
                        text = desc,
                        style = RixyTypography.Body,
                        color = RixyColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Contact info
                business.addressText?.let { address ->
                    ContactInfoRow(
                        icon = Icons.Default.LocationOn,
                        text = address
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                business.phone?.let { phone ->
                    ContactInfoRow(
                        icon = Icons.Default.Phone,
                        text = phone
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                business.openingHoursText?.let { hours ->
                    ContactInfoRow(
                        icon = Icons.Default.Schedule,
                        text = hours
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Contact buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RixyButton(
                        text = "WhatsApp",
                        onClick = onWhatsAppClick,
                        modifier = Modifier.weight(1f)
                    )
                    RixyButton(
                        text = "Llamar",
                        onClick = onPhoneClick,
                        variant = ButtonVariant.OUTLINE,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Listings section
        if (uiState.listings.isNotEmpty()) {
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = RixyColors.Border
                )
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Anuncios (${uiState.listings.size})",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(uiState.listings, key = { it.id }) { listing ->
                ListingCard(
                    title = listing.title,
                    imageUrl = listing.photoUrls?.firstOrNull(),
                    price = listing.productDetails?.priceAmount,
                    type = listing.type,
                    businessName = null, // Don't show business name in business profile
                    onClick = { onListingClick(listing) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RixyColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
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
    Column(
        modifier = modifier
    ) {
        // Header skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        
        Column(modifier = Modifier.padding(16.dp)) {
            // Name skeleton
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(28.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description skeleton
            repeat(3) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

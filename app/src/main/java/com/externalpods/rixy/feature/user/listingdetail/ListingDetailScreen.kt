package com.externalpods.rixy.feature.user.listingdetail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
// ListingTypeBadge is defined locally as ListingTypeBadgePublic
import com.externalpods.rixy.core.designsystem.components.ButtonVariant
import com.externalpods.rixy.core.designsystem.components.RixyButton
import com.externalpods.rixy.core.designsystem.components.SkeletonBox
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String,
    onBackClick: () -> Unit,
    onBusinessClick: (String) -> Unit,
    viewModel: ListingDetailViewModel = koinViewModel { parametersOf(listingId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Track view on first load
    LaunchedEffect(Unit) {
        viewModel.trackView()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", style = RixyTypography.H4) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Favorito",
                            tint = if (uiState.isFavorite) RixyColors.Brand else RixyColors.TextSecondary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                ListingDetailLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.error != null -> {
                EmptyErrorState(
                    message = uiState.error ?: "Error al cargar",
                    onRetry = { /* Retry loading */ },
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
                    onWhatsAppClick = { viewModel.onWhatsAppClick() },
                    modifier = Modifier.padding(paddingValues)
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
    onWhatsAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image carousel
        if (!listing.photoUrls.isNullOrEmpty()) {
            val pagerState = rememberPagerState(pageCount = { listing.photoUrls.size })
            
            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    AsyncImage(
                        model = listing.photoUrls[page],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Page indicator
                if (listing.photoUrls.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(listing.photoUrls.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) {
                                            RixyColors.Brand
                                        } else {
                                            RixyColors.Surface.copy(alpha = 0.5f)
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Type badge
            ListingTypeBadgePublic(type = listing.type)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            Text(
                text = listing.title,
                style = RixyTypography.H3,
                color = RixyColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price
            listing.productDetails?.priceAmount?.let { price ->
                Text(
                    text = "$ $price",
                    style = RixyTypography.H2,
                    color = RixyColors.Brand
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Business info
            listing.business?.let { business ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RixyColors.SurfaceVariant.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = business.name ?: "Negocio",
                            style = RixyTypography.BodyMedium,
                            color = RixyColors.TextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Description
            listing.description?.let { desc ->
                Text(
                    text = "Descripción",
                    style = RixyTypography.H4,
                    color = RixyColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    style = RixyTypography.Body,
                    color = RixyColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Contact buttons
            Text(
                text = "Contactar",
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
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
                    onClick = onContactClick,
                    variant = ButtonVariant.OUTLINE,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ListingTypeBadgePublic(type: ListingType) {
    val (backgroundColor, textColor, label) = when (type) {
        ListingType.PRODUCT -> Triple(
            RixyColors.Brand.copy(alpha = 0.1f),
            RixyColors.Brand,
            "Producto"
        )
        ListingType.SERVICE -> Triple(
            RixyColors.Action.copy(alpha = 0.1f),
            RixyColors.Action,
            "Servicio"
        )
        ListingType.EVENT -> Triple(
            RixyColors.Warning.copy(alpha = 0.1f),
            RixyColors.Warning,
            "Evento"
        )
        else -> Triple(
            RixyColors.SurfaceVariant,
            RixyColors.TextSecondary,
            "Otro"
        )
    }
    
    Text(
        text = label,
        style = RixyTypography.Caption,
        color = textColor,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun ListingDetailLoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        
        Column(modifier = Modifier.padding(16.dp)) {
            // Type badge skeleton
            SkeletonBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(20.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title skeleton
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price skeleton
            SkeletonBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(32.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Description skeleton
            repeat(4) {
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

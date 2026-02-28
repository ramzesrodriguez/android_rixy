package com.externalpods.rixy.feature.owner.listings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.components.EmptyStateView
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ListingStatus
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerListingsScreen(
    onBackClick: () -> Unit,
    onNewListing: () -> Unit,
    onEditListing: (String) -> Unit,
    viewModel: OwnerListingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ListingFilter.ALL) }

    val filteredListings = remember(uiState.listings, searchQuery, selectedFilter) {
        uiState.listings.filter { listing ->
            val matchesSearch = searchQuery.isBlank() || 
                listing.title.contains(searchQuery, ignoreCase = true) ||
                listing.categoryTag?.contains(searchQuery, ignoreCase = true) == true ||
                listing.type.name.contains(searchQuery, ignoreCase = true)
            
            val matchesFilter = when (selectedFilter) {
                ListingFilter.ALL -> true
                ListingFilter.PRODUCT -> listing.type == ListingType.PRODUCT
                ListingFilter.SERVICE -> listing.type == ListingType.SERVICE
                ListingFilter.EVENT -> listing.type == ListingType.EVENT
            }
            
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones", style = RixyTypography.H2) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = RixyColors.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNewListing) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nueva publicación",
                            tint = RixyColors.Brand
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RixyColors.Background
                )
            )
        },
        containerColor = RixyColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filter Chips
            FilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Listings
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = RixyColors.Brand)
                    }
                }
                filteredListings.isEmpty() -> {
                    EmptyStateView(
                        title = if (searchQuery.isNotBlank() || selectedFilter != ListingFilter.ALL) {
                            "No se encontraron publicaciones"
                        } else {
                            "No tienes publicaciones"
                        },
                        subtitle = if (searchQuery.isNotBlank() || selectedFilter != ListingFilter.ALL) {
                            "Intenta con otros filtros"
                        } else {
                            "Crea tu primera publicación para empezar a vender"
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredListings) { listing ->
                            OwnerListingCard(
                                listing = listing,
                                onClick = { onEditListing(listing.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(RixyColors.Surface),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = RixyColors.TextTertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Buscar por título, tipo o categoría",
                        style = RixyTypography.Body,
                        color = RixyColors.TextTertiary
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = RixyTypography.Body.copy(color = RixyColors.TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar",
                        tint = RixyColors.TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    selectedFilter: ListingFilter,
    onFilterSelected: (ListingFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = listOf(
        ListingFilter.ALL to "Todos",
        ListingFilter.PRODUCT to "Producto",
        ListingFilter.SERVICE to "Servicio",
        ListingFilter.EVENT to "Evento"
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(types) { (type, label) ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterSelected(type) },
                label = { Text(label, style = RixyTypography.Body) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RixyColors.Brand,
                    selectedLabelColor = RixyColors.White,
                    containerColor = RixyColors.White,
                    labelColor = RixyColors.TextPrimary
                ),
            )
        }
    }
}

@Composable
private fun OwnerListingCard(
    listing: Listing,
    onClick: () -> Unit
) {
    val statusColor = when (listing.status) {
        ListingStatus.PUBLISHED -> RixyColors.Community
        ListingStatus.PENDING_REVIEW -> RixyColors.Warning
        ListingStatus.DRAFT -> RixyColors.TextTertiary
        else -> RixyColors.TextSecondary
    }

    val statusText = when (listing.status) {
        ListingStatus.PUBLISHED -> "Publicado"
        ListingStatus.PENDING_REVIEW -> "En revisión"
        ListingStatus.DRAFT -> "Borrador"
        else -> listing.status?.name ?: "Borrador"
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Listing Image
            val imageUrl = listing.photoUrls?.firstOrNull()
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RixyColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (listing.type) {
                            ListingType.PRODUCT -> Icons.Default.Inventory
                            ListingType.SERVICE -> Icons.Default.Schedule
                            ListingType.EVENT -> Icons.Default.Event
                        },
                        contentDescription = null,
                        tint = RixyColors.TextTertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Listing Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = listing.title,
                    style = RixyTypography.BodyMedium,
                    color = RixyColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listing.categoryTag ?: listing.type.name,
                    style = RixyTypography.Caption,
                    color = RixyColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = statusText,
                            style = RixyTypography.CaptionSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    // Price if available
                    listing.productDetails?.priceAmount?.let { price ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${listing.productDetails.currency ?: "MXN"} $$price",
                            style = RixyTypography.Caption,
                            color = RixyColors.Brand,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    listing.serviceDetails?.priceAmount?.let { price ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${listing.serviceDetails.currency ?: "MXN"} $$price",
                            style = RixyTypography.Caption,
                            color = RixyColors.Brand,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    listing.eventDetails?.priceAmount?.let { price ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${listing.eventDetails.currency ?: "MXN"} $$price",
                            style = RixyTypography.Caption,
                            color = RixyColors.Brand,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Edit button
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(RixyColors.Brand.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = RixyColors.Brand,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = RixyColors.TextTertiary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private enum class ListingFilter {
    ALL, PRODUCT, SERVICE, EVENT
}

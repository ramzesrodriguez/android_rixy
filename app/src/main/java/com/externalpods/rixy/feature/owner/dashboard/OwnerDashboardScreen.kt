package com.externalpods.rixy.feature.owner.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.externalpods.rixy.core.designsystem.components.EmptyStateView
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.ListingStatus
import com.externalpods.rixy.feature.owner.business.BusinessEditorScreen
import com.externalpods.rixy.feature.owner.cityslots.OwnerCitySlotsScreen
import com.externalpods.rixy.feature.owner.featured.FeaturedCampaignsScreen
import com.externalpods.rixy.feature.owner.listings.ListingEditorScreen
import org.koin.androidx.compose.koinViewModel

private const val OWNER_HOME_ROUTE = "owner_home"
private const val OWNER_BUSINESS_EDITOR_ROUTE = "owner_business_editor"
private const val OWNER_LISTING_EDITOR_BASE_ROUTE = "owner_listing_editor"
private const val OWNER_FEATURED_ROUTE = "owner_featured"
private const val OWNER_CITY_SLOTS_ROUTE = "owner_city_slots"

@Composable
fun OwnerDashboardScreen(
    onBackToUser: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OWNER_HOME_ROUTE
    ) {
        composable(OWNER_HOME_ROUTE) {
            OwnerDashboardHome(
                onBackToUser = onBackToUser,
                onEditBusiness = { navController.navigate(OWNER_BUSINESS_EDITOR_ROUTE) },
                onNewListing = { navController.navigate(OWNER_LISTING_EDITOR_BASE_ROUTE) },
                onFeatured = { navController.navigate(OWNER_FEATURED_ROUTE) },
                onCitySlots = { navController.navigate(OWNER_CITY_SLOTS_ROUTE) },
                onEditListing = { listingId ->
                    navController.navigate("$OWNER_LISTING_EDITOR_BASE_ROUTE?listingId=$listingId")
                }
            )
        }

        composable(OWNER_BUSINESS_EDITOR_ROUTE) {
            BusinessEditorScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "$OWNER_LISTING_EDITOR_BASE_ROUTE?listingId={listingId}",
            arguments = listOf(
                navArgument("listingId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            ListingEditorScreen(
                listingId = backStackEntry.arguments?.getString("listingId"),
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(OWNER_FEATURED_ROUTE) {
            FeaturedCampaignsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(OWNER_CITY_SLOTS_ROUTE) {
            OwnerCitySlotsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OwnerDashboardHome(
    onBackToUser: () -> Unit,
    onEditBusiness: () -> Unit,
    onNewListing: () -> Unit,
    onFeatured: () -> Unit,
    onCitySlots: () -> Unit,
    onEditListing: (String) -> Unit,
    viewModel: OwnerDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Negocio", style = RixyTypography.H2) },
                navigationIcon = {
                    IconButton(onClick = onBackToUser) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = RixyColors.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
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
        when {
            uiState.isLoading && uiState.business == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RixyColors.Brand)
                }
            }

            uiState.error != null && uiState.business == null -> {
                ErrorViewGeneric(
                    message = uiState.error ?: "Error",
                    onRetry = viewModel::loadDashboard,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Section
                    item {
                        HeaderSection(
                            businessName = uiState.business?.name,
                            hasBusiness = uiState.business != null
                        )
                    }

                    // Business Card (if exists)
                    if (uiState.business != null) {
                        item {
                            BusinessCard(
                                businessName = uiState.business?.name ?: "",
                                businessStatus = uiState.business?.status?.name,
                                onClick = onEditBusiness
                            )
                        }
                    }

                    // Stats Grid
                    item {
                        StatsSection(
                            totalListings = uiState.listingsCount,
                            publishedListings = uiState.publishedCount,
                            totalViews = uiState.totalViews,
                            uniqueVisitors = uiState.uniqueVisitors,
                            pendingCount = uiState.pendingCount,
                            draftCount = uiState.draftCount
                        )
                    }

                    // Quick Actions
                    item {
                        QuickActionsSection(
                            hasBusiness = uiState.business != null,
                            onNewListing = onNewListing,
                            onEditBusiness = onEditBusiness,
                            onFeatured = onFeatured,
                            onCitySlots = onCitySlots
                        )
                    }

                    // Recent Listings
                    item {
                        RecentListingsSection(
                            listings = uiState.recentListings,
                            onEditListing = onEditListing,
                            onViewAll = { /* TODO: Navigate to all listings */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    businessName: String?,
    hasBusiness: Boolean
) {
    Column {
        Text(
            text = "Bienvenido",
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = businessName ?: "Comienza tu negocio",
            style = RixyTypography.H1,
            color = RixyColors.TextPrimary
        )
    }
}

@Composable
private fun BusinessCard(
    businessName: String,
    businessStatus: String?,
    onClick: () -> Unit
) {
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
            // Business icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(RixyColors.Brand.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = RixyColors.Brand,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Business info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = businessName,
                    style = RixyTypography.H4,
                    color = RixyColors.TextPrimary
                )
                businessStatus?.let {
                    Text(
                        text = it,
                        style = RixyTypography.Caption,
                        color = RixyColors.TextSecondary
                    )
                }
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = RixyColors.TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatsSection(
    totalListings: Int,
    publishedListings: Int,
    totalViews: Int,
    uniqueVisitors: Int,
    pendingCount: Int,
    draftCount: Int
) {
    Column {
        // Row 1: Listings and Views
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Publicaciones",
                value = totalListings.toString(),
                subtitle = "$publishedListings publicadas",
                icon = Icons.Default.Description,
                color = RixyColors.Brand,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Vistas",
                value = formatNumber(totalViews),
                subtitle = "$uniqueVisitors únicos",
                icon = Icons.Default.Visibility,
                color = RixyColors.Info,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Row 2: Pending and Drafts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Pendientes",
                value = pendingCount.toString(),
                subtitle = "En revisión",
                icon = Icons.Default.Schedule,
                color = RixyColors.Warning,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Borradores",
                value = draftCount.toString(),
                subtitle = "Sin publicar",
                icon = Icons.Default.Edit,
                color = RixyColors.TextTertiary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = value,
                style = RixyTypography.H3,
                color = RixyColors.TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = title,
                style = RixyTypography.Caption,
                color = RixyColors.TextSecondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = RixyTypography.CaptionSmall,
                color = RixyColors.TextTertiary
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    hasBusiness: Boolean,
    onNewListing: () -> Unit,
    onEditBusiness: () -> Unit,
    onFeatured: () -> Unit,
    onCitySlots: () -> Unit
) {
    Column {
        Text(
            text = "Acciones rápidas",
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary
        )
        Spacer(Modifier.height(12.dp))

        if (hasBusiness) {
            ActionButton(
                title = "Nueva Publicación",
                icon = Icons.Default.AddCircle,
                color = RixyColors.Brand,
                onClick = onNewListing
            )
            Spacer(Modifier.height(8.dp))
            ActionButton(
                title = "Editar Negocio",
                icon = Icons.Default.Business,
                color = RixyColors.Info,
                onClick = onEditBusiness
            )
            Spacer(Modifier.height(8.dp))
            ActionButton(
                title = "Promocionar",
                icon = Icons.Default.Campaign,
                color = RixyColors.Warning,
                onClick = onFeatured
            )
            Spacer(Modifier.height(8.dp))
            ActionButton(
                title = "Espacios Publicitarios",
                icon = Icons.Default.Stars,
                color = RixyColors.Success,
                onClick = onCitySlots
            )
        } else {
            ActionButton(
                title = "Crear mi Negocio",
                icon = Icons.Default.Store,
                color = RixyColors.Brand,
                onClick = onEditBusiness
            )
            Spacer(Modifier.height(8.dp))
            ActionButton(
                title = "Nueva Publicación",
                icon = Icons.Default.AddCircle,
                color = RixyColors.TextTertiary,
                onClick = {},
                enabled = false
            )
            Spacer(Modifier.height(8.dp))
            ActionButton(
                title = "Promocionar anuncio",
                icon = Icons.Default.Campaign,
                color = RixyColors.TextTertiary,
                onClick = {},
                enabled = false
            )
        }
    }
}

@Composable
private fun ActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) RixyColors.Surface else RixyColors.Background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) color else RixyColors.TextTertiary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                style = RixyTypography.BodyMedium,
                color = if (enabled) RixyColors.TextPrimary else RixyColors.TextTertiary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = RixyColors.TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RecentListingsSection(
    listings: List<com.externalpods.rixy.core.model.Listing>,
    onEditListing: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Publicaciones recientes",
                style = RixyTypography.H3,
                color = RixyColors.TextPrimary
            )
            TextButton(onClick = onViewAll) {
                Text(
                    text = "Ver todas",
                    style = RixyTypography.Caption,
                    color = RixyColors.Brand
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        if (listings.isEmpty()) {
            EmptyStateView(
                title = "No tienes publicaciones",
                subtitle = "Crea tu primera publicación para empezar a vender",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            listings.take(5).forEach { listing ->
                ListingRow(
                    listing = listing,
                    onClick = { onEditListing(listing.id) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ListingRow(
    listing: com.externalpods.rixy.core.model.Listing,
    onClick: () -> Unit
) {
    val statusColor = when (listing.status) {
        ListingStatus.PUBLISHED -> RixyColors.Community
        ListingStatus.PENDING_REVIEW -> RixyColors.Warning
        ListingStatus.DRAFT -> RixyColors.TextTertiary
        else -> RixyColors.TextSecondary
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = RixyColors.TextTertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listing.title,
                    style = RixyTypography.BodyMedium,
                    color = RixyColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listing.type.name,
                    style = RixyTypography.CaptionSmall,
                    color = RixyColors.TextSecondary
                )
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = listing.status?.name ?: "DRAFT",
                    style = RixyTypography.CaptionSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${number / 1000000}M"
        number >= 1000 -> "${number / 1000}k"
        else -> number.toString()
    }
}

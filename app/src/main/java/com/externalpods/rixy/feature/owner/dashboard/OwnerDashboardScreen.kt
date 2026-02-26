package com.externalpods.rixy.feature.owner.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonSize
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.components.DSMainHeader
import com.externalpods.rixy.core.designsystem.components.EmptyStateView
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
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
        containerColor = RixyColors.Background
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = RixyColors.Brand)
                }
            }

            uiState.error != null -> {
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
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DSMainHeader(
                            title = "Panel de Negocio"
                        )
                        DSButton(
                            title = "Cerrar",
                            onClick = onBackToUser,
                            variant = DSButtonVariant.OUTLINE,
                            size = DSButtonSize.SMALL
                        )
                    }

                    item {
                        Text(
                            text = uiState.business?.name ?: "Comienza tu negocio",
                            style = RixyTypography.H2,
                            color = RixyColors.TextPrimary
                        )
                    }

                    item {
                        StatsRow(
                            listings = uiState.listingsCount,
                            published = uiState.publishedCount,
                            draft = uiState.draftCount,
                            featured = uiState.featuredCount
                        )
                    }

                    item {
                        Text("Acciones rápidas", style = RixyTypography.H4, color = RixyColors.TextPrimary)
                    }

                    item {
                        ActionRow("Editar negocio", Icons.Default.Business, onEditBusiness)
                    }
                    item {
                        ActionRow("Nueva publicación", Icons.Default.Store, onNewListing)
                    }
                    item {
                        ActionRow("Campañas destacadas", Icons.Default.Campaign, onFeatured)
                    }
                    item {
                        ActionRow("Espacios de ciudad", Icons.Default.Star, onCitySlots)
                    }

                    item {
                        Text("Publicaciones recientes", style = RixyTypography.H4, color = RixyColors.TextPrimary)
                    }

                    if (uiState.analytics == null && uiState.listingsCount == 0) {
                        item {
                            EmptyStateView(
                                title = "No hay publicaciones",
                                subtitle = "Crea tu primera publicación para comenzar.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(uiState.recentListings, key = { it.id }) { listing ->
                            ActionRow(
                                title = listing.title,
                                icon = Icons.Default.List,
                                onClick = { onEditListing(listing.id) },
                                subtitle = listing.status?.name ?: "DRAFT"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(
    listings: Int,
    published: Int,
    draft: Int,
    featured: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Publicaciones", listings.toString(), Icons.Default.List, Modifier.weight(1f))
            StatCard("Publicadas", published.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Borradores", draft.toString(), Icons.Default.Store, Modifier.weight(1f))
            StatCard("Destacadas", featured.toString(), Icons.Default.Star, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Icon(icon, null, tint = RixyColors.Brand, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text(value, style = RixyTypography.H3, color = RixyColors.TextPrimary)
            Text(title, style = RixyTypography.Caption, color = RixyColors.TextSecondary)
        }
    }
}

@Composable
private fun ActionRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    subtitle: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = RixyColors.Brand)
            Spacer(Modifier.size(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = RixyTypography.BodyMedium, color = RixyColors.TextPrimary)
                subtitle?.let {
                    Text(it, style = RixyTypography.Caption, color = RixyColors.TextSecondary)
                }
            }
            Text(">", style = RixyTypography.H4, color = Color.Gray)
        }
    }
}

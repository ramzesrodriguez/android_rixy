package com.externalpods.rixy.feature.owner.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.EmptyErrorState
import com.externalpods.rixy.core.designsystem.components.RixyButton
import com.externalpods.rixy.core.designsystem.components.SkeletonBox
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(
    onNavigateToBusiness: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToFeatured: () -> Unit,
    onNavigateToCitySlots: () -> Unit,
    onNavigateToCreateListing: () -> Unit,
    viewModel: OwnerDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control", style = RixyTypography.H4) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateListing,
                containerColor = RixyColors.Brand
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo anuncio",
                    tint = RixyColors.White
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                DashboardLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.error != null -> {
                EmptyErrorState(
                    message = uiState.error ?: "Error al cargar",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                DashboardContent(
                    uiState = uiState,
                    onNavigateToBusiness = onNavigateToBusiness,
                    onNavigateToListings = onNavigateToListings,
                    onNavigateToFeatured = onNavigateToFeatured,
                    onNavigateToCitySlots = onNavigateToCitySlots,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: OwnerDashboardUiState,
    onNavigateToBusiness: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToFeatured: () -> Unit,
    onNavigateToCitySlots: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome message
        item {
            Text(
                text = "¡Hola, ${uiState.ownerName ?: "Propietario"}!",
                style = RixyTypography.H3,
                color = RixyColors.TextPrimary
            )
            Text(
                text = "Administra tus negocios y anuncios",
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary
            )
        }
        
        // Stats cards
        item {
            StatsRow(
                publishedCount = uiState.publishedCount,
                draftCount = uiState.draftCount,
                featuredCount = uiState.featuredCount
            )
        }
        
        // Quick actions
        item {
            Text(
                text = "Acciones rápidas",
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            QuickActionGrid(
                onNavigateToBusiness = onNavigateToBusiness,
                onNavigateToListings = onNavigateToListings,
                onNavigateToFeatured = onNavigateToFeatured,
                onNavigateToCitySlots = onNavigateToCitySlots
            )
        }
    }
}

@Composable
private fun StatsRow(
    publishedCount: Int,
    draftCount: Int,
    featuredCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Publicados",
            value = publishedCount.toString(),
            icon = Icons.Default.Store,
            color = RixyColors.Success,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Borradores",
            value = draftCount.toString(),
            icon = Icons.Default.GridView,
            color = RixyColors.Warning,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Destacados",
            value = featuredCount.toString(),
            icon = Icons.Default.Campaign,
            color = RixyColors.Brand,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = RixyTypography.H3,
                color = color
            )
            Text(
                text = title,
                style = RixyTypography.Caption,
                color = RixyColors.TextSecondary
            )
        }
    }
}

@Composable
private fun QuickActionGrid(
    onNavigateToBusiness: () -> Unit,
    onNavigateToListings: () -> Unit,
    onNavigateToFeatured: () -> Unit,
    onNavigateToCitySlots: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Mi Negocio",
            subtitle = "Editar información",
            icon = Icons.Default.Business,
            onClick = onNavigateToBusiness
        )
        QuickActionCard(
            title = "Mis Anuncios",
            subtitle = "Gestionar publicaciones",
            icon = Icons.Default.Store,
            onClick = onNavigateToListings
        )
        QuickActionCard(
            title = "Destacados",
            subtitle = "Promocionar anuncios",
            icon = Icons.Default.Campaign,
            onClick = onNavigateToFeatured
        )
        QuickActionCard(
            title = "Espacios Ciudad",
            subtitle = "Anuncios premium",
            icon = Icons.Default.GridView,
            onClick = onNavigateToCitySlots
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = RixyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RixyColors.Brand.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RixyColors.Brand,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.size(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = RixyTypography.BodyMedium,
                    color = RixyColors.TextPrimary
                )
                Text(
                    text = subtitle,
                    style = RixyTypography.Caption,
                    color = RixyColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DashboardLoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Welcome skeleton
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stats skeleton
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                SkeletonBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Actions skeleton
        repeat(4) {
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

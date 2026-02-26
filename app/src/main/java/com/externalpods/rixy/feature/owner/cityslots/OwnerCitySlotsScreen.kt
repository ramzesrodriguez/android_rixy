package com.externalpods.rixy.feature.owner.cityslots

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.*
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.CitySlotStatus
import com.externalpods.rixy.core.model.CitySlotSubscription
import com.externalpods.rixy.core.model.CitySlotType
import com.externalpods.rixy.core.model.Listing
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCitySlotsScreen(
    onBackClick: () -> Unit,
    viewModel: OwnerCitySlotsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.checkoutUrl) {
        val checkoutUrl = uiState.checkoutUrl ?: return@LaunchedEffect
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure {
            viewModel.clearError()
        }
        viewModel.onCheckoutCancelled()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("City Slots", style = RixyTypography.H2) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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
            uiState.isLoading && uiState.slotTypeGroups.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding), 
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RixyColors.Brand)
            }
            uiState.error != null && uiState.slotTypeGroups.isEmpty() -> EmptyErrorState(
                message = uiState.error ?: "Error",
                onRetry = { viewModel.loadSubscriptions() },
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // City Selector
                    item {
                        CitySelectorHeader(cityName = uiState.selectedCityName)
                        Spacer(Modifier.height(24.dp))
                    }
                    
                    // Page Title
                    item {
                        Text(
                            "Visibilidad de Ciudad",
                            style = RixyTypography.H1,
                            color = RixyColors.TextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Obtén ubicación premium en el inicio de ${uiState.selectedCityName}",
                            style = RixyTypography.Caption,
                            color = RixyColors.TextSecondary
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                    
                    // Available Slots Section (only configured slots with price > 0)
                    val configuredGroups = uiState.slotTypeGroups.filter { it.basePriceCents > 0 }
                    if (configuredGroups.isNotEmpty()) {
                        item {
                            Text(
                                "Slots disponibles",
                                style = RixyTypography.H3,
                                color = RixyColors.TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Selecciona una posición para comprar",
                                style = RixyTypography.Caption,
                                color = RixyColors.TextSecondary
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                        
                        items(
                            items = configuredGroups,
                            key = { it.type.name }
                        ) { group ->
                            SlotTypeCard(
                                group = group,
                                onPurchase = { slotIndex ->
                                    val availableSlot = uiState.availableSlots.find {
                                        it.type == group.type && it.slotIndex == slotIndex
                                    }
                                    availableSlot?.let { viewModel.openListingPickerForSlot(it) }
                                }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                    
                    // My Active Subscriptions Section
                    val activeSubscriptions = uiState.subscriptions.filter { 
                        it.status == CitySlotStatus.ACTIVE || it.status == CitySlotStatus.PENDING 
                    }
                    if (activeSubscriptions.isNotEmpty() || uiState.isLoading) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            SectionHeader(
                                title = "Tus slots activos",
                                subtitle = "Administra tu visibilidad actual en la ciudad"
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        
                        if (activeSubscriptions.isEmpty() && uiState.isLoading) {
                            item {
                                repeat(2) {
                                    SubscriptionCardSkeleton()
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        } else {
                            items(
                                items = activeSubscriptions,
                                key = { it.id }
                            ) { subscription ->
                                ActiveSubscriptionCard(
                                    subscription = subscription,
                                    onCancel = { viewModel.cancelSubscription(subscription.id) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                    
                    // Subscription History Section (only EXPIRED, CANCELED - NOT PENDING or ACTIVE)
                    val historySubscriptions = uiState.subscriptions.filter { 
                        it.status == CitySlotStatus.EXPIRED || 
                        it.status == CitySlotStatus.CANCELED
                    }
                    if (historySubscriptions.isNotEmpty() || uiState.subscriptions.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            SectionHeader(
                                title = "Historial de suscripciones",
                                subtitle = "Suscripciones pasadas y vencidas"
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        
                        if (historySubscriptions.isEmpty()) {
                            item {
                                EmptyStateSimple(
                                    message = "Aquí aparecerán tus suscripciones anteriores"
                                )
                            }
                        } else {
                            items(
                                items = historySubscriptions,
                                key = { it.id }
                            ) { subscription ->
                                SubscriptionHistoryCard(
                                    subscription = subscription,
                                    onRetry = { viewModel.retryPayment(subscription.id) },
                                    onRenew = { viewModel.renewSubscription(subscription.id) },
                                    onCancel = { viewModel.cancelSubscription(subscription.id) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                    
                    // Bottom padding
                    item {
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    if (uiState.showListingPicker) {
        ListingPickerSheet(
            listings = uiState.ownerListings,
            onDismiss = viewModel::dismissListingPicker,
            onSelectListing = { listing ->
                viewModel.purchaseSelectedSlot(listing.id)
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = RixyTypography.Caption,
            color = RixyColors.TextSecondary
        )
    }
}

@Composable
private fun CitySelectorHeader(
    cityName: String
) {
    Surface(
        onClick = { /* City picker */ },
        shape = RoundedCornerShape(28.dp),
        color = RixyColors.Surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = RixyColors.Brand,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = cityName,
                style = RixyTypography.BodyMedium,
                color = RixyColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = RixyColors.TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SlotTypeCard(
    group: SlotTypeGroup,
    onPurchase: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with type name, description and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.type.displayName,
                        style = RixyTypography.H4,
                        color = RixyColors.TextPrimary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = group.description,
                        style = RixyTypography.Caption,
                        color = RixyColors.TextSecondary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = group.formattedPrice,
                        style = RixyTypography.H4,
                        color = RixyColors.TextPrimary
                    )
                    Text(
                        text = "/30 days",
                        style = RixyTypography.CaptionSmall,
                        color = RixyColors.TextSecondary
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Slots grid - 2 columns
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                group.slots.chunked(2).forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { slot ->
                            SlotItem(
                                slot = slot,
                                onClick = { 
                                    if (slot.isAvailable) {
                                        onPurchase(slot.index)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowSlots.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotItem(
    slot: SlotInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (slot.isAvailable) {
        RixyColors.Community.copy(alpha = 0.05f)
    } else {
        RixyColors.Background
    }
    
    val borderColor = if (slot.isAvailable) {
        RixyColors.Community.copy(alpha = 0.3f)
    } else {
        RixyColors.Border
    }
    
    Surface(
        onClick = onClick,
        enabled = slot.isAvailable,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Slot number and status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Slot #${slot.index + 1}",
                    style = RixyTypography.BodyMedium,
                    color = RixyColors.TextPrimary
                )
                
                if (slot.isAvailable) {
                    StatusBadge(
                        text = "Disponible",
                        color = RixyColors.Community
                    )
                } else {
                    StatusBadge(
                        text = "Ocupado",
                        color = RixyColors.Monetization
                    )
                }
            }
            
            // Business info for occupied slots
            if (!slot.isAvailable) {
                Spacer(Modifier.height(8.dp))
                
                if (slot.businessName != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = RixyColors.TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = slot.businessName,
                            style = RixyTypography.CaptionSmall,
                            color = RixyColors.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                }
                
                if (slot.endAt != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = RixyColors.TextTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Hasta ${formatDate(slot.endAt)}",
                            style = RixyTypography.CaptionSmall,
                            color = RixyColors.TextTertiary
                        )
                    }
                }
            }
            
            // Buy button for available slots
            if (slot.isAvailable) {
                Spacer(Modifier.height(8.dp))
                DSButton(
                    title = "Comprar",
                    onClick = onClick,
                    size = DSButtonSize.SMALL
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = RixyTypography.CaptionSmall,
            color = color,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ActiveSubscriptionCard(
    subscription: CitySlotSubscription,
    onCancel: () -> Unit
) {
    val isActive = subscription.status == CitySlotStatus.ACTIVE
    val isPending = subscription.status == CitySlotStatus.PENDING
    val statusColor = when (subscription.status) {
        CitySlotStatus.ACTIVE -> RixyColors.Community
        CitySlotStatus.PENDING -> RixyColors.Monetization
        else -> RixyColors.TextSecondary
    }
    val statusText = subscription.status.name.uppercase()
    val cardBackground = if (isActive) {
        RixyColors.Community.copy(alpha = 0.03f)
    } else {
        RixyColors.Surface
    }
    val cardBorder = if (isActive) {
        androidx.compose.foundation.BorderStroke(1.dp, RixyColors.Community.copy(alpha = 0.2f))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, RixyColors.Border.copy(alpha = 0.5f))
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = cardBorder
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Icon + Title + Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon in circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (subscription.status) {
                            CitySlotStatus.ACTIVE -> Icons.Default.Check
                            CitySlotStatus.PENDING -> Icons.Default.Schedule
                            else -> Icons.Default.Help
                        },
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(Modifier.width(12.dp))
                
                // Title and subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subscription.slotType.displayName,
                        style = RixyTypography.BodyMedium,
                        color = RixyColors.TextPrimary
                    )
                    Text(
                        text = "${subscription.city?.name ?: "City"} • Slot #${subscription.slotIndex + 1}",
                        style = RixyTypography.Caption,
                        color = RixyColors.TextSecondary
                    )
                }
                
                Spacer(Modifier.width(8.dp))
                
                // Status badge
                StatusBadge(
                    text = statusText,
                    color = statusColor
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Expiration info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = RixyColors.TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (isPending) "Vence ${formatDate(subscription.endAt)}" else "Vence ${formatDate(subscription.endAt)}",
                    style = RixyTypography.Caption,
                    color = RixyColors.TextSecondary
                )
                
                subscription.daysRemaining?.let { days ->
                    Text(
                        text = " • ",
                        color = RixyColors.TextTertiary
                    )
                    Text(
                        text = "$days días restantes",
                        style = RixyTypography.Caption,
                        fontWeight = FontWeight.Medium,
                        color = if (days < 7) RixyColors.Monetization else RixyColors.Community
                    )
                }
            }
            
            // Promoted listing info
            subscription.assignment?.listing?.let { listing ->
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = RixyColors.Brand,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Promocionando: ${listing.title}",
                        style = RixyTypography.Caption,
                        color = RixyColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Cancel button
            Spacer(Modifier.height(12.dp))
            Row {
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = RixyColors.TextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Cancelar suscripción",
                        style = RixyTypography.Caption
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionHistoryCard(
    subscription: CitySlotSubscription,
    onRetry: () -> Unit,
    onRenew: () -> Unit,
    onCancel: () -> Unit
) {
    val (statusColor, statusIcon) = when (subscription.status) {
        CitySlotStatus.PENDING -> RixyColors.Monetization to Icons.Default.Schedule
        CitySlotStatus.EXPIRED -> RixyColors.TextSecondary to Icons.Default.CalendarToday
        CitySlotStatus.CANCELED -> RixyColors.Brand to Icons.Default.Close
        CitySlotStatus.PAUSED -> RixyColors.Action to Icons.Default.Pause
        else -> RixyColors.TextSecondary to Icons.Default.Help
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, RixyColors.Border.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Icon + Title + Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon in circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(Modifier.width(12.dp))
                
                // Title and subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subscription.slotType.displayName,
                        style = RixyTypography.BodyMedium,
                        color = RixyColors.TextPrimary
                    )
                    Text(
                        text = "${subscription.city?.name ?: "City"} • Slot #${subscription.slotIndex + 1}",
                        style = RixyTypography.Caption,
                        color = RixyColors.TextSecondary
                    )
                }
                
                Spacer(Modifier.width(8.dp))
                
                // Status badge
                StatusBadge(
                    text = subscription.status.name.uppercase(),
                    color = statusColor
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Date range
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = RixyColors.TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${formatDate(subscription.startAt)} – ${formatDate(subscription.endAt)}",
                    style = RixyTypography.Caption,
                    color = RixyColors.TextSecondary
                )
            }
            
            // Action buttons
            when (subscription.status) {
                CitySlotStatus.PENDING -> {
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Spacer(Modifier.weight(1f))
                        // Cancel button
                        TextButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = RixyColors.TextSecondary
                            )
                        ) {
                            Text("Cancelar", style = RixyTypography.Caption)
                        }
                        Spacer(Modifier.width(8.dp))
                        // Retry button
                        OutlinedButton(
                            onClick = onRetry,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RixyColors.Border)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Reintentar", style = RixyTypography.Caption)
                        }
                    }
                }
                CitySlotStatus.EXPIRED, CitySlotStatus.CANCELED -> {
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Spacer(Modifier.weight(1f))
                        // Renew button
                        OutlinedButton(
                            onClick = onRenew,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RixyColors.Border)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Renovar", style = RixyTypography.Caption)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun SubscriptionCardSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RixyColors.Surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skeleton circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RixyColors.Border)
                )
                Spacer(Modifier.width(12.dp))
                // Skeleton text
                Column {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(RixyColors.Border)
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(RixyColors.Border)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateSimple(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListingPickerSheet(
    listings: List<Listing>,
    onDismiss: () -> Unit,
    onSelectListing: (Listing) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = RixyColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Selecciona un anuncio",
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary
            )
            Spacer(Modifier.height(12.dp))
            if (listings.isEmpty()) {
                Text(
                    text = "No tienes anuncios activos para asignar",
                    style = RixyTypography.Body,
                    color = RixyColors.TextSecondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listings, key = { it.id }) { listing ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = RixyColors.Surface),
                            onClick = { onSelectListing(listing) }
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(listing.title, style = RixyTypography.BodyMedium)
                                Text(
                                    listing.type.name,
                                    style = RixyTypography.Caption,
                                    color = RixyColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("d MMM yyyy", Locale("es", "MX"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString.take(10)
    }
}

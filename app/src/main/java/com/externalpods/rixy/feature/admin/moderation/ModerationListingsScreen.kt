package com.externalpods.rixy.feature.admin.moderation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.*
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonSize
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.components.DSLabeledTextField
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Listing
import com.externalpods.rixy.core.model.ModerationAction
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationListingsScreen(
    onBackClick: () -> Unit,
    viewModel: ModerationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf<Listing?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderar Anuncios", style = RixyTypography.H4) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RixyColors.Brand)
            }
            uiState.error != null -> EmptyErrorState(
                message = uiState.error ?: "Error",
                onRetry = { viewModel.loadPendingListings() },
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            uiState.pendingListings.isEmpty() -> EmptyStateView(
                title = "Sin anuncios pendientes",
                subtitle = "No hay anuncios para moderar",
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.pendingListings, key = { it.id }) { listing ->
                    PendingListingCard(
                        listing = listing,
                        onApprove = { viewModel.moderateListing(listing.id, ModerationAction.APPROVE) },
                        onReject = { showDialog = listing }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
    
    showDialog?.let { listing ->
        RejectDialog(
            onConfirm = { reason ->
                viewModel.moderateListing(listing.id, ModerationAction.REJECT, reason)
                showDialog = null
            },
            onDismiss = { showDialog = null }
        )
    }
}

@Composable
private fun PendingListingCard(
    listing: Listing,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(listing.title, style = RixyTypography.BodyMedium)
            Text(listing.description ?: "", style = RixyTypography.Caption, color = RixyColors.TextSecondary, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DSButton(title = "Aprobar", onClick = onApprove, size = DSButtonSize.SMALL)
                DSButton(title = "Rechazar", onClick = onReject, variant = DSButtonVariant.OUTLINE, size = DSButtonSize.SMALL)
            }
        }
    }
}

@Composable
private fun RejectDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rechazar Anuncio") },
        text = { DSLabeledTextField(
                value = reason,
                onValueChange = { reason = it },
                label = "Razón",
                placeholder = "¿Por qué rechazas este anuncio?"
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(reason) }) { Text("Rechazar", color = RixyColors.Error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

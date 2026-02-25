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
import com.externalpods.rixy.core.model.Business
import com.externalpods.rixy.core.model.ModerationAction
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationBusinessesScreen(
    onBackClick: () -> Unit,
    viewModel: ModerationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf<Business?>(null) }
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("Moderar Negocios", style = RixyTypography.H4) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = RixyColors.Brand) }
            uiState.error != null -> EmptyErrorState(message = uiState.error ?: "Error", onRetry = { viewModel.loadPendingBusinesses() }, modifier = Modifier.fillMaxSize().padding(padding))
            uiState.pendingBusinesses.isEmpty() -> EmptyStateView(title = "Sin negocios pendientes", subtitle = "No hay negocios para moderar", modifier = Modifier.fillMaxSize().padding(padding))
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(uiState.pendingBusinesses, key = { it.id }) { business ->
                    PendingBusinessCard(
                        business = business,
                        onApprove = { viewModel.moderateBusiness(business.id, ModerationAction.APPROVE) },
                        onReject = { showDialog = business }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
    
    showDialog?.let { business ->
        RejectBusinessDialog(
            onConfirm = { reason ->
                viewModel.moderateBusiness(business.id, ModerationAction.REJECT, reason)
                showDialog = null
            },
            onDismiss = { showDialog = null }
        )
    }
}

@Composable
private fun PendingBusinessCard(business: Business, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(business.name, style = RixyTypography.BodyMedium)
            Text(business.description ?: "", style = RixyTypography.Caption, color = RixyColors.TextSecondary, maxLines = 2)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DSButton(title = "Aprobar", onClick = onApprove, size = DSButtonSize.SMALL)
                DSButton(title = "Rechazar", onClick = onReject, variant = DSButtonVariant.OUTLINE, size = DSButtonSize.SMALL)
            }
        }
    }
}

@Composable
private fun RejectBusinessDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rechazar Negocio") },
        text = { DSLabeledTextField(value = reason, onValueChange = { reason = it }, label = "Razón", placeholder = "¿Por qué rechazas este negocio?") },
        confirmButton = { TextButton(onClick = { onConfirm(reason) }) { Text("Rechazar", color = RixyColors.Error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EmptyStateView(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = RixyTypography.H4, color = RixyColors.TextPrimary)
            Text(subtitle, style = RixyTypography.Body, color = RixyColors.TextSecondary)
        }
    }
}

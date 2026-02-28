package com.externalpods.rixy.feature.admin.audit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.EmptyErrorState
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogsScreen(
    onBackClick: () -> Unit,
    viewModel: AuditLogsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs de Auditoría", style = RixyTypography.H4) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = RixyColors.Brand) }
            uiState.error != null -> EmptyErrorState(message = uiState.error ?: "Error", onRetry = { viewModel.loadLogs() }, modifier = Modifier.fillMaxSize().padding(padding))
            uiState.logs.isEmpty() -> EmptyStateView(title = "Sin registros", subtitle = "No hay logs de auditoría", modifier = Modifier.fillMaxSize().padding(padding))
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(uiState.logs, key = { it.id }) { log ->
                    LogCard(log = log)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LogCard(log: com.externalpods.rixy.core.model.AuditLog) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("${log.action} - ${log.entityType}", style = RixyTypography.BodyMedium)
            Text("Por: ${log.actorId}", style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            Text(log.createdAt ?: "", style = RixyTypography.CaptionSmall, color = RixyColors.TextTertiary)
        }
    }
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

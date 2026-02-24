package com.externalpods.rixy.feature.admin.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.RixyCard
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToModeration: () -> Unit,
    onNavigateToCities: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToAudit: () -> Unit,
    viewModel: AdminDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Dashboard", style = RixyTypography.H4) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Stats Row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Pendientes",
                    value = uiState.pendingModerationCount.toString(),
                    icon = Icons.Default.Warning,
                    color = RixyColors.Warning,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Usuarios",
                    value = uiState.totalUsers.toString(),
                    icon = Icons.Default.People,
                    color = RixyColors.Info,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Ingresos",
                    value = "$${uiState.monthlyRevenue}",
                    icon = Icons.Default.AttachMoney,
                    color = RixyColors.Success,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Quick Actions
            Text("Acciones Rápidas", style = RixyTypography.H4, color = RixyColors.TextPrimary)
            Spacer(Modifier.height(12.dp))
            
            ActionCard("Moderación", "Revisar anuncios pendientes", Icons.Default.Gavel, onNavigateToModeration)
            Spacer(Modifier.height(8.dp))
            ActionCard("Ciudades", "Gestionar ciudades", Icons.Default.LocationCity, onNavigateToCities)
            Spacer(Modifier.height(8.dp))
            ActionCard("Usuarios", "Administrar usuarios", Icons.Default.People, onNavigateToUsers)
            Spacer(Modifier.height(8.dp))
            ActionCard("Auditoría", "Ver logs de actividad", Icons.Default.History, onNavigateToAudit)
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color)
            Text(value, style = RixyTypography.H3, color = color)
            Text(title, style = RixyTypography.Caption, color = RixyColors.TextSecondary)
        }
    }
}

@Composable
private fun ActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(40.dp), tint = RixyColors.Brand)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = RixyTypography.BodyMedium)
                Text(subtitle, style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, null, tint = RixyColors.TextTertiary)
        }
    }
}

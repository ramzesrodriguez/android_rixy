package com.externalpods.rixy.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.AppMode
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    onModeChanged: (AppMode) -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSignOutDialog by remember { mutableStateOf(false) }
    
    // Handle sign out success
    LaunchedEffect(uiState.signOutSuccess) {
        if (uiState.signOutSuccess) {
            onNavigateToLogin()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", style = RixyTypography.H4) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // User info section
            UserInfoSection(email = uiState.userEmail)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Mode switcher
            ModeSwitcherSection(
                currentMode = uiState.currentMode,
                onModeSelected = { mode ->
                    viewModel.switchMode(mode)
                    onModeChanged(mode)
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = RixyColors.Border)
            Spacer(modifier = Modifier.height(16.dp))
            
            // General settings
            SettingsSection(title = "General") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Perfil",
                    subtitle = "Editar información",
                    onClick = { /* Navigate to profile */ }
                )
                SettingsItem(
                    icon = Icons.Default.Business,
                    title = "Mi Negocio",
                    subtitle = "Gestionar negocio",
                    onClick = { /* Navigate to business */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = RixyColors.Border)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Support section
            SettingsSection(title = "Soporte") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Centro de ayuda",
                    onClick = { /* Open help */ }
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    subtitle = "Versión 1.0.0",
                    onClick = { /* Show about */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = RixyColors.Border)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account section
            SettingsSection(title = "Cuenta") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Cerrar sesión",
                    titleColor = RixyColors.Error,
                    showArrow = false,
                    onClick = { showSignOutDialog = true }
                )
            }
        }
    }
    
    // Sign out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.signOut()
                    }
                ) {
                    Text("Cerrar sesión", color = RixyColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun UserInfoSection(
    email: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = RixyColors.Brand
        )
        
        Spacer(modifier = Modifier.size(16.dp))
        
        Column {
            Text(
                text = "Usuario",
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary
            )
            Text(
                text = email.ifEmpty { "Invitado" },
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary
            )
        }
    }
}

@Composable
private fun ModeSwitcherSection(
    currentMode: AppMode,
    onModeSelected: (AppMode) -> Unit
) {
    Column {
        Text(
            text = "Modo de uso",
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ModeOption(
            mode = AppMode.USER,
            title = "Usuario",
            subtitle = "Explorar anuncios y negocios",
            isSelected = currentMode == AppMode.USER,
            onClick = { onModeSelected(AppMode.USER) }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ModeOption(
            mode = AppMode.OWNER,
            title = "Propietario",
            subtitle = "Gestionar negocio y anuncios",
            isSelected = currentMode == AppMode.OWNER,
            onClick = { onModeSelected(AppMode.OWNER) }
        )
    }
}

@Composable
private fun ModeOption(
    mode: AppMode,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Seleccionado",
                tint = RixyColors.Brand
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = RixyTypography.Caption,
            color = RixyColors.TextSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        content()
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: androidx.compose.ui.graphics.Color = RixyColors.TextPrimary,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RixyColors.TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.size(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = RixyTypography.BodyMedium,
                color = titleColor
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = RixyTypography.Caption,
                    color = RixyColors.TextSecondary
                )
            }
        }
        
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = RixyColors.TextTertiary
            )
        }
    }
}

package com.externalpods.rixy.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Security

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.components.SectionButton
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * SettingsScreen - User settings/profile screen (mirrors iOS SettingsView)
 */
@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    onModeChanged: () -> Unit,
    onBackClick: (() -> Unit)?
) {
    Scaffold(
        topBar = {
            com.externalpods.rixy.core.designsystem.components.DSTopBar(
                title = "Perfil",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // Profile Header
            ProfileHeaderSection(
                name = "Usuario Invitado",
                email = "Inicia sesión para acceder a tu cuenta",
                onLoginClick = onNavigateToLogin
            )
            
            HorizontalDivider(
                color = RixyColors.Border,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Account Section
            SectionHeader("Cuenta")
            SectionButton(
                icon = Icons.Default.Person,
                title = "Editar Perfil",
                onClick = { },
                showChevron = true
            )
            SectionButton(
                icon = Icons.Outlined.Notifications,
                title = "Notificaciones",
                onClick = { },
                showChevron = true
            )
            SectionButton(
                icon = Icons.Outlined.Security,
                title = "Seguridad",
                onClick = { },
                showChevron = true
            )
            
            HorizontalDivider(
                color = RixyColors.Border,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Business Section
            SectionHeader("Negocios")
            SectionButton(
                icon = Icons.Default.Business,
                title = "Modo Propietario",
                subtitle = "Administra tu negocio",
                onClick = onModeChanged,
                showChevron = true
            )
            
            HorizontalDivider(
                color = RixyColors.Border,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Support Section
            SectionHeader("Soporte")
            SectionButton(
                icon = Icons.Default.Info,
                title = "Ayuda",
                onClick = { },
                showChevron = true
            )
            SectionButton(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Cerrar Sesión",
                onClick = { },
                showChevron = false,
                isDestructive = true
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = RixyTypography.CaptionSmall,
        color = RixyColors.TextTertiary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileHeaderSection(
    name: String,
    email: String,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = RixyColors.BrandLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = RixyColors.Brand,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = RixyTypography.Title3
            )
            Text(
                text = email,
                style = RixyTypography.BodySmall,
                color = RixyColors.TextSecondary
            )
        }
        
        // Login/Edit button
        Surface(
            onClick = onLoginClick,
            shape = CircleShape,
            color = RixyColors.BrandLight
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Login",
                tint = RixyColors.Brand,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

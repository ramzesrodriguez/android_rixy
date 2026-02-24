package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSErrorView - iOS-style Error State Component
 * 
 * Replicates iOS error states with:
 * - Warning/Error icon
 * - Error title
 * - Description message
 * - Retry action button
 * - Optional secondary action
 */
@Composable
fun DSErrorView(
    icon: ImageVector,
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = RixyColors.Error,
    secondaryAction: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = title,
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Message
        Text(
            text = message,
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Retry button
        DSButton(
            title = "Reintentar",
            onClick = onRetry,
            variant = DSButtonVariant.PRIMARY,
            icon = Icons.Default.Refresh
        )
        
        // Secondary action
        secondaryAction?.let {
            Spacer(modifier = Modifier.height(12.dp))
            it()
        }
    }
}

/**
 * Pre-configured error states
 */

@Composable
fun ErrorViewNetwork(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSErrorView(
        icon = Icons.Filled.SignalWifiOff,
        title = "Sin conexión",
        message = "Parece que no tienes conexión a internet. Verifica tu red e intenta de nuevo.",
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
fun ErrorViewServer(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSErrorView(
        icon = Icons.Outlined.CloudOff,
        title = "Error del servidor",
        message = "Hubo un problema con nuestros servidores. Por favor, intenta de nuevo en unos momentos.",
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
fun ErrorViewGeneric(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSErrorView(
        icon = Icons.Default.Warning,
        title = "Algo salió mal",
        message = message,
        onRetry = onRetry,
        modifier = modifier
    )
}

@Composable
fun ErrorViewNotFound(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            style = RixyTypography.H1.copy(fontSize = 64.sp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No encontrado",
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "El contenido que buscas no existe o ha sido eliminado",
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        DSButton(
            title = "Volver",
            onClick = onBack,
            variant = DSButtonVariant.PRIMARY
        )
    }
}

@Composable
fun ErrorViewUnauthorized(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔒",
            style = RixyTypography.H1.copy(fontSize = 64.sp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Inicia sesión",
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Necesitas iniciar sesión para ver este contenido",
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        DSButton(
            title = "Iniciar sesión",
            onClick = onLogin,
            variant = DSButtonVariant.PRIMARY
        )
    }
}

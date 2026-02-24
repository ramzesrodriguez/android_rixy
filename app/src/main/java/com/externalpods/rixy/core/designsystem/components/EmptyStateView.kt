package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * Empty state view with icon, title, subtitle and optional CTA
 */
@Composable
fun EmptyStateView(
    title: String,
    subtitle: String? = null,
    icon: ImageVector = Icons.Default.Info,
    action: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = RixyColors.TextTertiary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
        
        action?.let {
            Spacer(modifier = Modifier.height(24.dp))
            it()
        }
    }
}

/**
 * Empty state for search results
 */
@Composable
fun EmptySearchResults(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "No se encontraron resultados",
        subtitle = "No hay anuncios que coincidan con \"$query\"",
        icon = Icons.Default.Search,
        action = {
            RixyButton(
                text = "Limpiar búsqueda",
                onClick = onClearSearch,
                variant = ButtonVariant.OUTLINE
            )
        },
        modifier = modifier
    )
}

/**
 * Empty state for error
 */
@Composable
fun EmptyErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateView(
        title = "Algo salió mal",
        subtitle = message,
        icon = Icons.Default.Info,
        action = {
            RixyButton(
                text = "Reintentar",
                onClick = onRetry
            )
        },
        modifier = modifier
    )
}

package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * EmptyState Components - iOS-style empty states
 */

@Composable
fun EmptyStateFavorites(
    onBrowseClick: () -> Unit
) {
    EmptyStateView(
        icon = Icons.Outlined.FavoriteBorder,
        title = "No tienes favoritos",
        message = "Guarda tus publicaciones favoritas para verlas aquí",
        actionText = "Explorar",
        onActionClick = onBrowseClick
    )
}

@Composable
fun EmptyStateOrders() {
    EmptyStateView(
        icon = Icons.Outlined.ShoppingBag,
        title = "No tienes pedidos",
        message = "Tus pedidos aparecerán aquí",
        actionText = null,
        onActionClick = null
    )
}

@Composable
fun EmptyStateNoCity(
    onSelectCity: () -> Unit
) {
    EmptyStateView(
        icon = Icons.Outlined.LocationOn,
        title = "Selecciona una ciudad",
        message = "Elige una ciudad primero para explorar negocios y publicaciones",
        actionText = "Seleccionar ciudad",
        onActionClick = onSelectCity
    )
}

@Composable
private fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    actionText: String?,
    onActionClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = RixyColors.BrandLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RixyColors.Brand,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = title,
            style = RixyTypography.Title2,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Message
        Text(
            text = message,
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            DSButton(
                title = actionText,
                onClick = onActionClick
            )
        }
    }
}

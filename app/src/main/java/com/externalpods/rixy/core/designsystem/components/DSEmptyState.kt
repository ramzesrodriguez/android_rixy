package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
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
 * DSEmptyState - iOS-style Empty State Component
 * 
 * Replicates iOS EmptyStateView with:
 * - Large emoji/icon at top
 * - Title in H3
 * - Description in Body
 * - Optional action button
 * - Centered layout
 * 
 * Variants:
 * - Favorites: Heart icon, "No tienes favoritos"
 * - Search: Search icon, "Sin resultados"
 * - Location: Location icon, "Selecciona una ciudad"
 * - Orders: Bag icon, "No tienes pedidos"
 * - Generic: Custom icon and message
 */
@Composable
fun DSEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    iconColor: Color = RixyColors.TextTertiary,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
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
        
        // Action button
        action?.let {
            Spacer(modifier = Modifier.height(32.dp))
            it()
        }
    }
}

/**
 * Empty state with emoji (for more personality)
 */
@Composable
fun DSEmptyStateWithEmoji(
    emoji: String,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji
        Text(
            text = emoji,
            style = RixyTypography.H1.copy(fontSize = 64.sp)
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
        
        // Action button
        action?.let {
            Spacer(modifier = Modifier.height(32.dp))
            it()
        }
    }
}

/**
 * Pre-configured empty states for common scenarios
 */

@Composable
fun EmptyStateFavorites(
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSEmptyState(
        icon = Icons.Outlined.FavoriteBorder,
        title = "No tienes favoritos",
        message = "Guarda tus publicaciones favoritas para verlas aquí",
        modifier = modifier,
        action = {
            DSButton(
                title = "Explorar",
                onClick = onBrowseClick,
                variant = DSButtonVariant.PRIMARY
            )
        }
    )
}

@Composable
fun EmptyStateSearch(
    query: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSEmptyState(
        icon = Icons.Outlined.Search,
        title = "Sin resultados",
        message = if (query.isEmpty()) {
            "Usa la barra de búsqueda para encontrar productos, servicios y eventos"
        } else {
            "No encontramos resultados para \"$query\". Intenta con otros términos."
        },
        modifier = modifier,
        action = if (query.isNotEmpty()) {
            {
                DSOutlineButton(
                    title = "Limpiar búsqueda",
                    onClick = onClearSearch
                )
            }
        } else null
    )
}

@Composable
fun EmptyStateOrders(
    onBrowseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSEmptyStateWithEmoji(
        emoji = "🛍️",
        title = "No tienes pedidos",
        message = "Tus pedidos aparecerán aquí cuando realices una compra",
        modifier = modifier,
        action = {
            DSButton(
                title = "Explorar",
                onClick = onBrowseClick,
                variant = DSButtonVariant.PRIMARY
            )
        }
    )
}

@Composable
fun EmptyStateNoCity(
    onSelectCity: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSEmptyState(
        icon = Icons.Outlined.LocationOn,
        title = "Selecciona una ciudad",
        message = "Elige una ciudad primero para explorar negocios y publicaciones",
        modifier = modifier,
        action = {
            DSButton(
                title = "Seleccionar ciudad",
                onClick = onSelectCity,
                variant = DSButtonVariant.PRIMARY
            )
        }
    )
}

@Composable
fun EmptyStateNotifications(
    modifier: Modifier = Modifier
) {
    DSEmptyStateWithEmoji(
        emoji = "🔔",
        title = "Sin notificaciones",
        message = "No tienes notificaciones nuevas. Te avisaremos cuando haya novedades.",
        modifier = modifier
    )
}

@Composable
fun EmptyStateNoBusiness(
    onCreateBusiness: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSEmptyStateWithEmoji(
        emoji = "🏪",
        title = "No tienes negocios",
        message = "Crea tu primer negocio para comenzar a publicar productos, servicios y eventos",
        modifier = modifier,
        action = {
            DSButton(
                title = "Crear negocio",
                onClick = onCreateBusiness,
                variant = DSButtonVariant.PRIMARY,
                icon = Icons.Default.Add
            )
        }
    )
}

@Composable
fun EmptyStateNoListings(
    onCreateListing: () -> Unit,
    modifier: Modifier = Modifier
) {
    DSEmptyStateWithEmoji(
        emoji = "📦",
        title = "No tienes publicaciones",
        message = "Crea tu primera publicación para mostrar tus productos o servicios",
        modifier = modifier,
        action = {
            DSButton(
                title = "Crear publicación",
                onClick = onCreateListing,
                variant = DSButtonVariant.PRIMARY,
                icon = Icons.Default.Add
            )
        }
    )
}

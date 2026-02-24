package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.externalpods.rixy.core.designsystem.modifiers.iosShadow
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyShadows
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSAlertDialog - iOS-style Alert Dialog
 * 
 * Replicates iOS UIAlertController:
 * - Centered title
 * - Centered message
 * - Vertical buttons for primary actions
 * - Horizontal divider between buttons
 * - Rounded corners (20dp like iOS)
 * - Subtle shadow
 */
@Composable
fun DSAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(RixyColors.Surface)
                .iosShadow(style = RixyShadows.Modal)
        ) {
            // Title and message
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = RixyTypography.H4,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = message,
                    style = RixyTypography.Body,
                    color = RixyColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            
            // Divider
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(RixyColors.Border)
            )
            
            // Buttons
            if (dismissButton != null) {
                // Two buttons: horizontal
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dismiss button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        dismissButton()
                    }
                    
                    // Vertical divider
                    Spacer(
                        modifier = Modifier
                            .width(0.5.dp)
                            .height(48.dp)
                            .background(RixyColors.Border)
                    )
                    
                    // Confirm button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        confirmButton()
                    }
                }
            } else {
                // Single button: full width
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    confirmButton()
                }
            }
        }
    }
}

/**
 * Standard iOS-style alert with text buttons
 */
@Composable
fun DSAlert(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "OK",
    dismissText: String? = null
) {
    DSAlertDialog(
        title = title,
        message = message,
        onDismiss = onDismiss,
        confirmButton = {
            Text(
                text = confirmText,
                style = RixyTypography.Button.copy(
                    color = RixyColors.Brand
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        },
        dismissButton = dismissText?.let {
            {
                Text(
                    text = it,
                    style = RixyTypography.Button.copy(
                        color = RixyColors.TextSecondary
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Destructive alert (delete confirmation)
 */
@Composable
fun DSAlertDestructive(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    deleteText: String = "Eliminar",
    cancelText: String = "Cancelar"
) {
    DSAlertDialog(
        title = title,
        message = message,
        onDismiss = onDismiss,
        confirmButton = {
            Text(
                text = deleteText,
                style = RixyTypography.Button.copy(
                    color = RixyColors.Error
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        },
        dismissButton = {
            Text(
                text = cancelText,
                style = RixyTypography.Button.copy(
                    color = RixyColors.Brand
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        },
        modifier = modifier
    )
}

/**
 * Action Sheet - iOS-style bottom sheet for multiple options
 * Simplified version
 */
@Composable
fun DSActionSheet(
    title: String?,
    message: String?,
    onDismiss: () -> Unit,
    actions: List<ActionSheetItem>,
    cancelAction: ActionSheetItem
) {
    // This would typically be implemented with a BottomSheet
    // For now, we'll use a simple Dialog as placeholder
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(RixyColors.Surface)
        ) {
            // Title and message
            if (title != null || message != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    title?.let {
                        Text(
                            text = it,
                            style = RixyTypography.H4,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    message?.let {
                        if (title != null) Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = RixyTypography.Subtext,
                            color = RixyColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(RixyColors.Border)
                )
            }
            
            // Actions
            actions.forEach { action ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = action.title,
                        style = RixyTypography.BodyMedium.copy(
                            color = if (action.isDestructive) RixyColors.Error else action.color
                        )
                    )
                }
                
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(RixyColors.Border)
                )
            }
            
            // Cancel button (highlighted)
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RixyColors.Surface)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cancelAction.title,
                    style = RixyTypography.Button,
                    color = RixyColors.Brand
                )
            }
        }
    }
}

data class ActionSheetItem(
    val title: String,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
    val color: androidx.compose.ui.graphics.Color = RixyColors.Brand
)


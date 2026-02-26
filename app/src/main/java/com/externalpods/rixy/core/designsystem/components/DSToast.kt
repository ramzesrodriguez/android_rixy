package com.externalpods.rixy.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import kotlinx.coroutines.delay

/**
 * DSToast - iOS-style Toast/Snackbar Component
 * 
 * Replicates iOS Toast notifications:
 * - Rounded corners (12dp)
 * - Icon + message
 * - Auto-dismiss with animation
 * - Positioned at top (iOS style) or bottom
 * - Different styles: Success, Error, Warning, Info
 */
enum class ToastType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

@Composable
fun DSToast(
    message: String,
    type: ToastType,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000
) {
    val (icon, backgroundColor, contentColor) = when (type) {
        ToastType.SUCCESS -> Triple(
            Icons.Default.CheckCircle,
            RixyColors.Success,
            Color.White
        )
        ToastType.ERROR -> Triple(
            Icons.Default.Error,
            RixyColors.Error,
            Color.White
        )
        ToastType.WARNING -> Triple(
            Icons.Default.Warning,
            RixyColors.Warning,
            RixyColors.Structure
        )
        ToastType.INFO -> Triple(
            Icons.Default.Info,
            RixyColors.Brand,
            Color.White
        )
    }
    
    // Auto dismiss
    if (visible) {
        LaunchedEffect(Unit) {
            delay(durationMillis)
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = message,
                    style = RixyTypography.Body,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * iOS-style Snackbar Host
 * Positioned at top like iOS toasts
 */
@Composable
fun DSTopSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        snackbar = { data ->
            // Determine type from message prefix
            val (type, cleanMessage) = when {
                data.visuals.message.startsWith("[SUCCESS]") -> 
                    ToastType.SUCCESS to data.visuals.message.removePrefix("[SUCCESS] ")
                data.visuals.message.startsWith("[ERROR]") -> 
                    ToastType.ERROR to data.visuals.message.removePrefix("[ERROR] ")
                data.visuals.message.startsWith("[WARNING]") -> 
                    ToastType.WARNING to data.visuals.message.removePrefix("[WARNING] ")
                else -> ToastType.INFO to data.visuals.message
            }
            
            val (icon, backgroundColor, contentColor) = when (type) {
                ToastType.SUCCESS -> Triple(
                    Icons.Default.CheckCircle,
                    RixyColors.Success,
                    Color.White
                )
                ToastType.ERROR -> Triple(
                    Icons.Default.Error,
                    RixyColors.Error,
                    Color.White
                )
                ToastType.WARNING -> Triple(
                    Icons.Default.Warning,
                    RixyColors.Warning,
                    RixyColors.Structure
                )
                ToastType.INFO -> Triple(
                    Icons.Default.Info,
                    RixyColors.Structure,
                    Color.White
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = cleanMessage,
                        style = RixyTypography.Body,
                        color = contentColor
                    )
                }
            }
        }
    )
}

/**
 * Helper to show toast from coroutine scope
 * Usage: scope.launch { showToast(snackbarHostState, "message", ToastType.SUCCESS) }
 */
suspend fun showToast(
    snackbarHostState: SnackbarHostState,
    message: String,
    type: ToastType = ToastType.INFO
) {
    val prefix = when (type) {
        ToastType.SUCCESS -> "[SUCCESS] "
        ToastType.ERROR -> "[ERROR] "
        ToastType.WARNING -> "[WARNING] "
        ToastType.INFO -> ""
    }
    val duration = when (type) {
        ToastType.ERROR -> SnackbarDuration.Long
        else -> SnackbarDuration.Short
    }
    snackbarHostState.showSnackbar(
        message = "$prefix$message",
        duration = duration
    )
}

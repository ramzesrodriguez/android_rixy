package com.externalpods.rixy.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.AppMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModePickerSheet(
    selectedMode: AppMode,
    availableModes: List<AppMode>,
    onSelectMode: (AppMode) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = RixyColors.Background,
        scrimColor = RixyColors.Black.copy(alpha = 0.35f),
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        modifier = Modifier.fillMaxHeight(0.98f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(RixyColors.Background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = RixyColors.White,
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Cancelar",
                        style = RixyTypography.H4.copy(color = RixyColors.Brand),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                Text(
                    text = "Seleccionar modo",
                    style = RixyTypography.H3,
                    color = RixyColors.TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp),
                shape = RoundedCornerShape(24.dp),
                color = RixyColors.White
            ) {
                Column {
                    availableModes.forEachIndexed { index, mode ->
                        ModeRow(
                            title = mode.displayLabel,
                            isSelected = selectedMode == mode,
                            onClick = { onSelectMode(mode) }
                        )
                        if (index < availableModes.lastIndex) {
                            HorizontalDivider(
                                color = RixyColors.Border,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = RixyColors.Brand
            )
        }
    }
}

private val AppMode.displayLabel: String
    get() = when (this) {
        AppMode.USER -> "Usuario"
        AppMode.OWNER -> "Negocio"
        AppMode.ADMIN -> "Administrador"
    }

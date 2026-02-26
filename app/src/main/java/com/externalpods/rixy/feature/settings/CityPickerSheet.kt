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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.components.DSSearchField
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerSheet(
    selectedCityId: String?,
    cities: List<City>,
    searchQuery: String,
    isLoading: Boolean,
    error: String?,
    onSearchQueryChange: (String) -> Unit,
    onSelectCity: (City) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                    text = "Cambiar ciudad",
                    style = RixyTypography.H3,
                    color = RixyColors.TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp),
                shape = RoundedCornerShape(24.dp),
                color = RixyColors.White
            ) {
                when {
                    isLoading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = RixyColors.Brand)
                        }
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = error,
                                style = RixyTypography.Body,
                                color = RixyColors.TextSecondary
                            )
                            Text(
                                text = "Reintentar",
                                style = RixyTypography.H4.copy(color = RixyColors.Brand),
                                modifier = Modifier.clickable(onClick = onRetry)
                            )
                        }
                    }
                    cities.isEmpty() -> {
                        Text(
                            text = "No se encontraron ciudades",
                            style = RixyTypography.Body,
                            color = RixyColors.TextSecondary,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                    else -> {
                        LazyColumn {
                            itemsIndexed(cities, key = { _, city -> city.id }) { index, city ->
                                CityRow(
                                    city = city,
                                    isSelected = selectedCityId == city.id,
                                    showDivider = index < cities.lastIndex,
                                    onClick = { onSelectCity(city) }
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Bottom
            ) {
                DSSearchField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    onSearch = { },
                    placeholder = "Buscar ciudad...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun CityRow(
    city: City,
    isSelected: Boolean,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = city.name,
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
        Text(
            text = "${city.state ?: ""}, ${city.country ?: "MX"}".trim().trim(','),
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
        if (showDivider) {
            HorizontalDivider(
                color = RixyColors.Border,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

package com.externalpods.rixy.feature.user.cityselector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.CityCard
import com.externalpods.rixy.core.designsystem.components.CityCardSkeleton
import com.externalpods.rixy.core.designsystem.components.DSSearchField
import com.externalpods.rixy.core.designsystem.components.DSTopBar
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import org.koin.androidx.compose.koinViewModel

@Composable
fun CitySelectorScreen(
    onCitySelected: (City) -> Unit,
    onBackClick: (() -> Unit)? = null,
    viewModel: CitySelectorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        containerColor = RixyColors.Background,
        modifier = Modifier.padding(top = 16.dp),
        topBar = {
            DSTopBar(
                title = "Selecciona tu ciudad",
                onBackClick = onBackClick,
                backgroundColor = RixyColors.Background,
                titleStyle = RixyTypography.H2
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            DSSearchField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                onSearch = { /* Already filtering on query change */ },
                placeholder = "Buscar ciudad...",
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content
            when {
                uiState.isLoading -> {
                    CitiesLoadingState()
                }
                uiState.error != null -> {
                    ErrorViewGeneric(
                        message = uiState.error ?: "Error desconocido",
                        onRetry = viewModel::loadCities,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.filteredCities.isEmpty() -> {
                    EmptyCitiesState(query = uiState.searchQuery)
                }
                else -> {
                    CitiesGrid(
                        cities = uiState.filteredCities,
                        onCitySelected = { city ->
                            viewModel.selectCity(city)
                            onCitySelected(city)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CitiesGrid(
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cities) { city ->
            CityCard(
                name = city.name,
                imageUrl = city.heroImageUrl,
                listingCount = city.listingCount,
                onClick = { onCitySelected(city) }
            )
        }
    }
}

@Composable
private fun CitiesLoadingState(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            CityCardSkeleton()
        }
    }
}

@Composable
private fun EmptyCitiesState(
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "☹\uFE0F",
                style = RixyTypography.H1
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (query.isEmpty()) {
                    "No hay ciudades disponibles"
                } else {
                    "No se encontraron ciudades para \"$query\""
                },
                style = RixyTypography.H4,
                color = RixyColors.TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

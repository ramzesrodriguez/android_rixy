package com.externalpods.rixy.feature.admin.cities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.*
import com.externalpods.rixy.core.designsystem.components.DSLabeledTextField
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.City
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesManagementScreen(
    onBackClick: () -> Unit,
    viewModel: CitiesManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingCity by remember { mutableStateOf<City?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ciudades", style = RixyTypography.H4) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) { Icon(Icons.Default.Add, null) }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RixyColors.Brand)
            }
            uiState.error != null -> EmptyErrorState(message = uiState.error ?: "Error", onRetry = { viewModel.loadCities() }, modifier = Modifier.fillMaxSize().padding(padding))
            uiState.cities.isEmpty() -> EmptyStateView(title = "Sin ciudades", subtitle = "Agrega una ciudad para comenzar", modifier = Modifier.fillMaxSize().padding(padding))
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(uiState.cities, key = { it.id }) { city ->
                    CityAdminCard(city = city, onEdit = { editingCity = city }, onToggleActive = { viewModel.toggleCityActive(city.id, !city.isActive!!) })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
    
    if (showCreateDialog) {
        CityDialog(onConfirm = { name, slug -> viewModel.createCity(name, slug); showCreateDialog = false }, onDismiss = { showCreateDialog = false })
    }
    
    editingCity?.let { city ->
        CityDialog(city = city, onConfirm = { name, slug -> viewModel.updateCity(city.id, name, slug); editingCity = null }, onDismiss = { editingCity = null })
    }
}

@Composable
private fun CityAdminCard(city: City, onEdit: () -> Unit, onToggleActive: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(city.name, style = RixyTypography.BodyMedium)
                Text("/${city.slug}", style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            }
            Switch(checked = city.isActive == true, onCheckedChange = { onToggleActive() })
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null) }
        }
    }
}

@Composable
private fun CityDialog(city: City? = null, onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(city?.name ?: "") }
    var slug by remember { mutableStateOf(city?.slug ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (city == null) "Crear Ciudad" else "Editar Ciudad") },
        text = {
            Column {
                DSLabeledTextField(value = name, onValueChange = { name = it }, label = "Nombre", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                DSLabeledTextField(value = slug, onValueChange = { slug = it }, label = "Slug", modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(name, slug) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

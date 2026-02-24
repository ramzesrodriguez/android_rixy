package com.externalpods.rixy.feature.admin.users

import androidx.compose.foundation.layout.*
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
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.Owner
import com.externalpods.rixy.core.model.OwnerRole
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersManagementScreen(
    onBackClick: () -> Unit,
    viewModel: UsersManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var editingUser by remember { mutableStateOf<Owner?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios", style = RixyTypography.H4) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = RixyColors.Brand) }
            uiState.error != null -> EmptyErrorState(message = uiState.error ?: "Error", onRetry = { viewModel.loadUsers() }, modifier = Modifier.fillMaxSize().padding(padding))
            uiState.users.isEmpty() -> EmptyStateView(title = "Sin usuarios", subtitle = "No hay usuarios registrados", modifier = Modifier.fillMaxSize().padding(padding))
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(uiState.users, key = { it.id }) { user ->
                    UserCard(user = user, onEditRole = { editingUser = user }, onSuspend = { viewModel.suspendUser(user.id) })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
    
    editingUser?.let { user ->
        RoleDialog(
            currentRole = user.role ?: OwnerRole.OWNER,
            onConfirm = { role -> viewModel.updateUserRole(user.id, role); editingUser = null },
            onDismiss = { editingUser = null }
        )
    }
}

@Composable
private fun UserCard(user: Owner, onEditRole: () -> Unit, onSuspend: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(user.email, style = RixyTypography.BodyMedium)
                Text("Rol: ${user.role}", style = RixyTypography.Caption, color = RixyColors.TextSecondary)
            }
            IconButton(onClick = onEditRole) { Icon(Icons.Default.Edit, null) }
            IconButton(onClick = onSuspend) { Icon(Icons.Default.Block, null, tint = RixyColors.Error) }
        }
    }
}

@Composable
private fun RoleDialog(currentRole: OwnerRole, onConfirm: (OwnerRole) -> Unit, onDismiss: () -> Unit) {
    var selected by remember { mutableStateOf(currentRole) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Rol") },
        text = {
            Column {
                OwnerRole.entries.forEach { role ->
                    Row(Modifier.fillMaxWidth().clickable { selected = role }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == role, onClick = { selected = role })
                        Spacer(Modifier.width(8.dp))
                        Text(role.name)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(selected) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

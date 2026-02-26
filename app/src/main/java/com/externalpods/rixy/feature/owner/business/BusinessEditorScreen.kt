package com.externalpods.rixy.feature.owner.business

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSLabeledTextField
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessEditorScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: BusinessEditorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handle save success
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }
    
    val isEditing = uiState.business != null
    
    // Image pickers
    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadLogo(it) }
    }
    
    val headerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadHeader(it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (isEditing) "Editar Negocio" else "Crear Negocio",
                        style = RixyTypography.H4
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RixyColors.Brand)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Image Section
                HeaderImageSection(
                    headerUrl = uiState.headerUrl,
                    isUploading = uiState.isUploadingImage,
                    onClick = { headerPicker.launch("image/*") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Logo Section
                LogoSection(
                    logoUrl = uiState.logoUrl,
                    isUploading = uiState.isUploadingImage,
                    onClick = { logoPicker.launch("image/*") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = RixyColors.Border)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Form Fields
                Text(text = "Información del Negocio",
                    style = RixyTypography.H4,
                    color = RixyColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Business Name
                DSLabeledTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre del negocio *",
                    placeholder = "Ej: Mi Tienda",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                DSLabeledTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Descripción",
                    placeholder = "Describe tu negocio...",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = RixyColors.Border)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Contact Info
                Text(text = "Información de Contacto",
                    style = RixyTypography.H4,
                    color = RixyColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Address
                DSLabeledTextField(
                    value = uiState.address,
                    onValueChange = viewModel::onAddressChange,
                    label = "Dirección",
                    placeholder = "Calle, número, colonia...",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Phone
                DSLabeledTextField(
                    value = uiState.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = "Teléfono",
                    placeholder = "(55) 1234-5678",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // WhatsApp
                DSLabeledTextField(
                    value = uiState.whatsapp,
                    onValueChange = viewModel::onWhatsappChange,
                    label = "WhatsApp",
                    placeholder = "+52 55 1234 5678",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = RixyColors.Border)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Online Presence
                Text(text = "Presencia Online",
                    style = RixyTypography.H4,
                    color = RixyColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Website
                DSLabeledTextField(
                    value = uiState.website,
                    onValueChange = viewModel::onWebsiteChange,
                    label = "Sitio Web",
                    placeholder = "https://mitienda.com",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Opening Hours
                DSLabeledTextField(
                    value = uiState.openingHours,
                    onValueChange = viewModel::onOpeningHoursChange,
                    label = "Horario de Atención",
                    placeholder = "Lun-Vie: 9am-6pm, Sab: 10am-4pm",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Error Message
                uiState.error?.let { error ->
                    Text(text = error,
                        style = RixyTypography.Body,
                        color = RixyColors.Error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // Save Button
                DSButton(
                    title = if (isEditing) "Guardar Cambios" else "Crear Negocio",
                    onClick = viewModel::saveBusiness,
                    isLoading = uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeaderImageSection(
    headerUrl: String?,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(RixyColors.SurfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (headerUrl != null) {
            AsyncImage(
                model = headerUrl,
                contentDescription = "Header image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Overlay for edit hint
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RixyColors.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = RixyColors.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(text = "Cambiar imagen",
                        style = RixyTypography.Body,
                        color = RixyColors.White
                    )
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null,
                    tint = RixyColors.TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Agregar imagen de portada",
                    style = RixyTypography.Body,
                    color = RixyColors.TextSecondary
                )
            }
        }
        
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RixyColors.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RixyColors.White)
            }
        }
    }
}

@Composable
private fun LogoSection(
    logoUrl: String?,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(RixyColors.SurfaceVariant)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (logoUrl != null) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Edit overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RixyColors.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = RixyColors.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = RixyColors.TextSecondary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Logo",
                        style = RixyTypography.Caption,
                        color = RixyColors.TextSecondary
                    )
                }
            }
            
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RixyColors.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RixyColors.White)
                }
            }
        }
    }
}

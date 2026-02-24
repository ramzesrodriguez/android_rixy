package com.externalpods.rixy.feature.owner.listings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.externalpods.rixy.core.designsystem.components.ButtonVariant
import com.externalpods.rixy.core.designsystem.components.RixyButton
import com.externalpods.rixy.core.designsystem.components.RixyTextField
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import com.externalpods.rixy.core.model.ListingType
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingEditorScreen(
    listingId: String? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: ListingEditorViewModel = koinViewModel { parametersOf(listingId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }
    
    val isEditing = listingId != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState.currentStep) {
                            1 -> "Tipo de Anuncio"
                            2 -> "Información Básica"
                            3 -> "Detalles"
                            else -> "Crear Anuncio"
                        },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { uiState.currentStep / 3f },
                modifier = Modifier.fillMaxWidth(),
                color = RixyColors.Brand
            )
            
            // Step indicator
            StepIndicator(currentStep = uiState.currentStep)
            
            // Content
            when (uiState.currentStep) {
                1 -> StepOneTypeSelection(
                    selectedType = uiState.selectedType,
                    onTypeSelected = viewModel::onTypeSelected
                )
                2 -> StepTwoBasicInfo(
                    uiState = uiState,
                    onTitleChange = viewModel::onTitleChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onImageSelected = viewModel::uploadImage,
                    onRemoveImage = viewModel::removeImage,
                    onNext = { viewModel.goToStep(3) },
                    onBack = { viewModel.goToStep(1) }
                )
                3 -> StepThreeDetails(
                    uiState = uiState,
                    viewModel = viewModel,
                    onSave = viewModel::saveListing,
                    onBack = { viewModel.goToStep(2) }
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..3).forEach { step ->
            val isActive = step == currentStep
            val isCompleted = step < currentStep
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when {
                                isActive -> RixyColors.Brand
                                isCompleted -> RixyColors.Success
                                else -> RixyColors.SurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = RixyColors.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = step.toString(),
                            style = RixyTypography.BodyMedium,
                            color = if (isActive) RixyColors.White else RixyColors.TextSecondary
                        )
                    }
                }
                Text(
                    text = when (step) {
                        1 -> "Tipo"
                        2 -> "Básico"
                        else -> "Detalles"
                    },
                    style = RixyTypography.CaptionSmall,
                    color = if (isActive || isCompleted) RixyColors.TextPrimary else RixyColors.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun StepOneTypeSelection(
    selectedType: ListingType?,
    onTypeSelected: (ListingType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "¿Qué quieres publicar?",
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Selecciona el tipo de anuncio que mejor describe tu publicación",
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Product Option
        TypeOptionCard(
            title = "Producto",
            description = "Vende artículos físicos como ropa, electrónica, muebles, etc.",
            icon = Icons.Default.Inventory,
            isSelected = selectedType == ListingType.PRODUCT,
            onClick = { onTypeSelected(ListingType.PRODUCT) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Service Option
        TypeOptionCard(
            title = "Servicio",
            description = "Ofrece tus servicios profesionales como plomería, diseño, clases, etc.",
            icon = Icons.Default.Schedule,
            isSelected = selectedType == ListingType.SERVICE,
            onClick = { onTypeSelected(ListingType.SERVICE) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Event Option
        TypeOptionCard(
            title = "Evento",
            description = "Promociona eventos como conciertos, talleres, ferias, etc.",
            icon = Icons.Default.Event,
            isSelected = selectedType == ListingType.EVENT,
            onClick = { onTypeSelected(ListingType.EVENT) }
        )
    }
}

@Composable
private fun TypeOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) RixyColors.Brand.copy(alpha = 0.1f) else RixyColors.Surface)
            .border(
                width = 2.dp,
                color = if (isSelected) RixyColors.Brand else RixyColors.Border,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) RixyColors.Brand else RixyColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) RixyColors.White else RixyColors.TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = RixyTypography.H4,
                color = if (isSelected) RixyColors.Brand else RixyColors.TextPrimary
            )
            Text(
                text = description,
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = RixyColors.Brand
            )
        }
    }
}

@Composable
private fun StepTwoBasicInfo(
    uiState: ListingEditorUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        RixyTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = "Título *",
            placeholder = "Ej: iPhone 13 Pro Max 256GB",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Description
        RixyTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = "Descripción",
            placeholder = "Describe tu producto o servicio...",
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category
        RixyTextField(
            value = uiState.category,
            onValueChange = onCategoryChange,
            label = "Categoría",
            placeholder = "Ej: Electrónica, Servicios, etc.",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = RixyColors.Border)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Photos Section
        Text(
            text = "Fotos",
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Photo grid
        PhotoGrid(
            photoUrls = uiState.photoUrls,
            isUploading = uiState.isUploadingImage,
            onAddPhoto = { imagePicker.launch("image/*") },
            onRemovePhoto = onRemoveImage
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Error
        uiState.error?.let { error ->
            Text(
                text = error,
                style = RixyTypography.Body,
                color = RixyColors.Error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RixyButton(
                text = "Atrás",
                onClick = onBack,
                variant = ButtonVariant.OUTLINE,
                modifier = Modifier.weight(1f)
            )
            RixyButton(
                text = "Siguiente",
                onClick = onNext,
                enabled = uiState.title.isNotBlank(),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PhotoGrid(
    photoUrls: List<String>,
    isUploading: Boolean,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Add photo button
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(RixyColors.SurfaceVariant)
                .clickable(onClick = onAddPhoto),
            contentAlignment = Alignment.Center
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    color = RixyColors.Brand,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Agregar foto",
                    tint = RixyColors.TextSecondary
                )
            }
        }
        
        // Photo thumbnails
        photoUrls.take(4).forEach { url ->
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Remove button
                IconButton(
                    onClick = { onRemovePhoto(url) },
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = RixyColors.Error,
                        modifier = Modifier
                            .size(16.dp)
                            .background(RixyColors.White, RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepThreeDetails(
    uiState: ListingEditorUiState,
    viewModel: ListingEditorViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState.selectedType) {
            ListingType.PRODUCT -> ProductDetailsForm(uiState = uiState, viewModel = viewModel)
            ListingType.SERVICE -> ServiceDetailsForm(uiState = uiState, viewModel = viewModel)
            ListingType.EVENT -> EventDetailsForm(uiState = uiState, viewModel = viewModel)
            null -> {}
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Error
        uiState.error?.let { error ->
            Text(
                text = error,
                style = RixyTypography.Body,
                color = RixyColors.Error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RixyButton(
                text = "Atrás",
                onClick = onBack,
                variant = ButtonVariant.OUTLINE,
                modifier = Modifier.weight(1f)
            )
            RixyButton(
                text = if (uiState.existingListing != null) "Guardar Cambios" else "Publicar",
                onClick = onSave,
                isLoading = uiState.isSaving,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProductDetailsForm(
    uiState: ListingEditorUiState,
    viewModel: ListingEditorViewModel
) {
    Column {
        Text(
            text = "Detalles del Producto",
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Price
        RixyTextField(
            value = uiState.productPrice,
            onValueChange = { viewModel._uiState.update { it.copy(productPrice = it.productPrice) } },
            label = "Precio",
            placeholder = "0.00",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stock
        RixyTextField(
            value = uiState.stockQuantity,
            onValueChange = {},
            label = "Cantidad en stock",
            placeholder = "10",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ServiceDetailsForm(
    uiState: ListingEditorUiState,
    viewModel: ListingEditorViewModel
) {
    Column {
        Text(
            text = "Detalles del Servicio",
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Price
        RixyTextField(
            value = uiState.servicePrice,
            onValueChange = {},
            label = "Precio",
            placeholder = "0.00",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Duration
        RixyTextField(
            value = uiState.durationMinutes,
            onValueChange = {},
            label = "Duración (minutos)",
            placeholder = "60",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EventDetailsForm(
    uiState: ListingEditorUiState,
    viewModel: ListingEditorViewModel
) {
    Column {
        Text(
            text = "Detalles del Evento",
            style = RixyTypography.H4,
            color = RixyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Start Date
        RixyTextField(
            value = uiState.eventStartDate,
            onValueChange = {},
            label = "Fecha de inicio",
            placeholder = "2024-12-31T18:00:00",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Venue
        RixyTextField(
            value = uiState.venueName,
            onValueChange = {},
            label = "Lugar",
            placeholder = "Nombre del venue",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Price
        RixyTextField(
            value = uiState.eventPrice,
            onValueChange = {},
            label = "Precio",
            placeholder = "0.00",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

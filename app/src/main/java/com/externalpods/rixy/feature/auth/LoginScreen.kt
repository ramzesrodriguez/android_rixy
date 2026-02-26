package com.externalpods.rixy.feature.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSButtonSize
import com.externalpods.rixy.core.designsystem.components.DSButtonVariant
import com.externalpods.rixy.core.designsystem.components.DSCard
import com.externalpods.rixy.core.designsystem.components.DSTextField
import com.externalpods.rixy.core.designsystem.components.ErrorViewGeneric
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

/**
 * LoginScreen - iOS-style Login Screen
 * 
 * Replicates iOS LoginView with:
 * - Centered logo/icon
 * - Clean form with icons
 * - Keyboard-friendly layout
 * - Clear error states
 * - Link to register
 * - Smooth transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    
    // Handle successful login
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onLoginSuccess()
        }
    }
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("") },
                navigationIcon = {
                    onBackClick?.let { onBack ->
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors( RixyColors.Background)
            )
        }
    ) { paddingValues ->
        when {
            uiState.error != null && uiState.email.isEmpty() -> {
                // Full screen error for initial load errors
                ErrorViewGeneric(
                    message = uiState.error ?: "Error al iniciar sesión",
                    onRetry = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                LoginContent(
                    email = uiState.email,
                    onEmailChange = viewModel::onEmailChange,
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                    isPasswordVisible = uiState.isPasswordVisible,
                    onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
                    isLoading = uiState.isLoading,
                    emailError = uiState.emailError,
                    passwordError = uiState.passwordError,
                    generalError = uiState.error,
                    onLoginClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.login()
                    },
                    onNavigateToRegister = onNavigateToRegister,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isLoading: Boolean,
    emailError: String?,
    passwordError: String?,
    generalError: String?,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Icon
        Text(
            text = "🔑",
            style = RixyTypography.H1
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Brand Name
        Text(
            text = "KeyCity",
            style = RixyTypography.H1
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = "Inicia sesión para continuar",
            style = RixyTypography.Body,
            color = RixyColors.TextSecondary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Error card (if any)
        generalError?.let { error ->
            DSCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RixyColors.Error.copy(alpha = 0.1f),
                border = null
            ) {
                Text(
                    text = error,
                    style = RixyTypography.Body,
                    color = RixyColors.Error
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Email field
        DSTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "Correo electrónico",
            icon = Icons.Default.Email,
            isError = emailError != null,
            errorMessage = emailError,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password field
        DSTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Contraseña",
            icon = Icons.Default.Lock,
            isError = passwordError != null,
            errorMessage = passwordError,
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login button
        DSButton(
            title = if (isLoading) "" else "Iniciar sesión",
            onClick = onLoginClick,
            variant = DSButtonVariant.PRIMARY,
            size = DSButtonSize.LARGE,
            fillWidth = true,
            isLoading = isLoading
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Register link
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                style = RixyTypography.Body,
                color = RixyColors.TextSecondary
            )
            
            Text(
                text = "Regístrate",
                style = RixyTypography.Button.copy(
                    color = RixyColors.Brand
                ),
                modifier = Modifier.clickable(onClick = onNavigateToRegister)
            )
        }
        
        // Forgot password link
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "¿Olvidaste tu contraseña?",
            style = RixyTypography.Body,
            color = RixyColors.Brand,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(onClick = { /* Navigate to forgot password */ })
        )
    }
}

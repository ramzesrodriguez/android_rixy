package com.externalpods.rixy.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.externalpods.rixy.core.designsystem.components.DSButton
import com.externalpods.rixy.core.designsystem.components.DSLabeledTextField
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handle navigation on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo / Brand
        Text(text = "Rixy",
            style = RixyTypography.H1,
            color = RixyColors.Brand,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(text = "Conecta con tu ciudad",
            style = RixyTypography.BodyLarge,
            color = RixyColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Title
        Text(text = "Crear cuenta",
            style = RixyTypography.H3,
            color = RixyColors.TextPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Email field
        DSLabeledTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Correo electrónico",
            placeholder = "tu@email.com",
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password field
        DSLabeledTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Contraseña",
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm password field
        DSLabeledTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Confirmar contraseña",
            isError = uiState.confirmPasswordError != null,
            errorMessage = uiState.confirmPasswordError,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // General error
        uiState.error?.let { error ->
            Text(text = error,
                style = RixyTypography.Body,
                color = RixyColors.Error,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Register button
        DSButton(
            title = "Crear cuenta",
            onClick = viewModel::register,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login link
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = RixyColors.TextSecondary)) {
                append("¿Ya tienes cuenta? ")
            }
            pushStringAnnotation(tag = "login", annotation = "login")
            withStyle(
                style = SpanStyle(
                    color = RixyColors.Brand,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Inicia sesión")
            }
            pop()
        }
        
        ClickableText(text = annotatedString,
            style = RixyTypography.Body,
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "login",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onNavigateToLogin()
                }
            }
        )
    }
}

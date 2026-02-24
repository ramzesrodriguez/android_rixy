package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyDimensions
import com.externalpods.rixy.core.designsystem.theme.RixyShapes
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

@Composable
fun RixyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val borderColor = when {
        isError -> RixyColors.Error
        isFocused -> RixyColors.Brand.copy(alpha = 0.4f)
        else -> RixyColors.Border
    }
    
    val borderWidth = if (isFocused) 2.dp else 1.dp
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(RixyDimensions.InputHeight),
        placeholder = placeholder?.let { { Text(it, style = RixyTypography.Body) } },
        label = label?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = RixyColors.TextSecondary
                )
            }
        },
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = errorMessage?.let { { Text(it, color = RixyColors.Error) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
            onGo = { onImeAction() },
            onSearch = { onImeAction() },
            onSend = { onImeAction() }
        ),
        singleLine = singleLine,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        shape = RixyShapes.Input,
        textStyle = RixyTypography.Body,
        interactionSource = interactionSource,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RixyColors.Brand,
            unfocusedBorderColor = RixyColors.Border,
            errorBorderColor = RixyColors.Error,
            focusedContainerColor = RixyColors.Surface,
            unfocusedContainerColor = RixyColors.Surface,
            focusedTextColor = RixyColors.TextPrimary,
            unfocusedTextColor = RixyColors.TextPrimary,
            focusedLeadingIconColor = RixyColors.TextSecondary,
            unfocusedLeadingIconColor = RixyColors.TextSecondary,
            disabledBorderColor = RixyColors.Border.copy(alpha = 0.5f),
            disabledTextColor = RixyColors.TextTertiary
        )
    )
}

@Composable
fun RixySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar...",
    onSearch: () -> Unit = {}
) {
    RixyTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        imeAction = ImeAction.Search,
        onImeAction = onSearch,
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = RixyColors.TextSecondary
                    )
                }
            }
        } else null
    )
}

@Composable
fun RixyPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Contraseña",
    isVisible: Boolean = false,
    onVisibilityToggle: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null
) {
    RixyTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = if (isVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Text(
                    text = if (isVisible) "‍" else "‍",
                    color = RixyColors.TextSecondary,
                    style = RixyTypography.BodyMedium
                )
            }
        }
    )
}

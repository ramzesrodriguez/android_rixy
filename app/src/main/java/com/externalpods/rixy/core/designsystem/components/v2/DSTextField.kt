package com.externalpods.rixy.core.designsystem.components.v2

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.animations.RixyAnimations
import com.externalpods.rixy.core.designsystem.modifiers.focusGlow
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

/**
 * DSTextField - iOS-style Text Input Component
 * 
 * Replicates DSTextField.swift with:
 * - 44dp height (not 56dp Material3)
 * - 8dp border radius
 * - Focus glow effect (shadow with brand color)
 * - Animated border width and color
 * - Icon support on left
 * 
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param placeholder Placeholder text (shown when empty)
 * @param modifier Modifier to apply
 * @param icon Optional leading icon (like iOS SF Symbol)
 * @param isError Whether the field has an error
 * @param errorMessage Error message to display
 * @param keyboardType Keyboard type
 * @param imeAction IME action
 * @param onImeAction Callback when IME action triggered
 * @param singleLine Whether input is single line
 * @param maxLines Maximum number of lines
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only
 * @param visualTransformation Visual transformation (for passwords)
 */
@Composable
fun DSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
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
    
    // Animated border width: 2dp when focused, 1dp otherwise
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = RixyAnimations.springDefault(),
        label = "border_width"
    )
    
    // Animated border color
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> RixyColors.Error
            isFocused -> RixyColors.Brand.copy(alpha = 0.4f)
            else -> RixyColors.Border
        },
        animationSpec = RixyAnimations.springDefault(),
        label = "border_color"
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp) // iOS height, not 56dp Material3
                // Focus glow effect
                .then(
                    if (isFocused) {
                        Modifier.drawBehind {
                            // Draw shadow glow
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        RixyColors.Brand.copy(alpha = 0.18f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = size.width.coerceAtLeast(size.height) * 0.8f
                                )
                            )
                        }
                    } else Modifier
                )
                .background(RixyColors.Surface, RoundedCornerShape(8.dp))
                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Leading icon (like iOS)
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = RixyColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                // Text input
                Box(modifier = Modifier.weight(1f)) {
                    // Placeholder
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = RixyTypography.Body,
                            color = RixyColors.TextTertiary
                        )
                    }
                    
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = RixyTypography.Body.copy(
                            color = RixyColors.TextPrimary
                        ),
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
                        interactionSource = interactionSource,
                        cursorBrush = SolidColor(RixyColors.Brand)
                    )
                }
            }
        }
        
        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = RixyTypography.Caption,
                color = RixyColors.Error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * DSTextField with label above
 */
@Composable
fun DSLabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    icon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = RixyTypography.Label,
            color = RixyColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        DSTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            icon = icon,
            isError = isError,
            errorMessage = errorMessage,
            keyboardType = keyboardType,
            imeAction = imeAction,
            onImeAction = onImeAction,
            singleLine = singleLine
        )
    }
}

/**
 * Password text field with visibility toggle
 */
@Composable
fun DSPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Contraseña",
    icon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val visibilityIcon: @Composable (() -> Unit)? = {
        IconButton(
            onClick = { isVisible = !isVisible },
            modifier = Modifier.size(32.dp)
        ) {
            Text(
                text = if (isVisible) "🙈" else "👁️",
                style = RixyTypography.BodyMedium
            )
        }
    }
    
    // For now, use DSTextField without trailing icon support
    // In production, you'd extend DSTextField to support trailing icons
    DSTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        icon = icon,
        isError = isError,
        errorMessage = errorMessage,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        onImeAction = onImeAction,
        visualTransformation = if (isVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

/**
 * Search text field with clear button
 */
@Composable
fun DSSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar...",
    onSearch: () -> Unit = {}
) {
    DSTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        // icon = Icons.Default.Search, // Add when importing Icons
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search,
        onImeAction = onSearch
    )
}

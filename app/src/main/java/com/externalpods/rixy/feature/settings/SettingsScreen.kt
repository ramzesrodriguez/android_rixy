package com.externalpods.rixy.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.components.DSMainHeader
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyTypography

@Composable
fun SettingsScreen(
    isAuthenticated: Boolean,
    selectedCityName: String?,
    userEmail: String? = null,
    languageLabel: String = "Español",
    currentModeLabel: String = "Usuario",
    canUseOwnerMode: Boolean = false,
    onNavigateToLogin: () -> Unit,
    onModeChanged: () -> Unit,
    onSignOut: () -> Unit,
    onBackClick: (() -> Unit)?,
    onLanguageClick: (() -> Unit)? = null,
    onChangeCityClick: () -> Unit = {}
) {
    // Note: No Scaffold here - parent (UserTabBarView) already has one
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 0.dp)
            .background(RixyColors.Background)
    ) {
        DSMainHeader(
            title = "Perfil",
            onBackClick = onBackClick
        )

        if (isAuthenticated) {
            AuthenticatedProfileContent(
                userEmail = userEmail,
                selectedCityName = selectedCityName,
                languageLabel = languageLabel,
                currentModeLabel = currentModeLabel,
                canUseOwnerMode = canUseOwnerMode,
                onModeChanged = onModeChanged,
                onSignOut = onSignOut,
                onLanguageClick = onLanguageClick,
                onChangeCityClick = onChangeCityClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            )
        } else {
            GuestProfileContent(
                selectedCityName = selectedCityName,
                languageLabel = languageLabel,
                onNavigateToLogin = onNavigateToLogin,
                onLanguageClick = onLanguageClick,
                onChangeCityClick = onChangeCityClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp)
                    .padding(bottom = 80.dp)
            )
        }
    }
}

@Composable
private fun GuestProfileContent(
    selectedCityName: String?,
    languageLabel: String,
    onNavigateToLogin: () -> Unit,
    onLanguageClick: (() -> Unit)?,
    onChangeCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GuestSectionTitle("Idioma")
        SettingsPillRow(
            title = "Idioma",
            trailing = languageLabel,
            titleColor = RixyColors.Brand,
            onClick = onLanguageClick,
            showChevron = onLanguageClick != null
        )

        GuestSectionTitle("Ciudad seleccionada")
        SettingsPillRow(
            title = selectedCityName ?: "Seleccionar ciudad",
            trailing = if (selectedCityName.isNullOrBlank()) "" else "Cambiar",
            trailingColor = RixyColors.Brand,
            onClick = onChangeCityClick,
            showChevron = false
        )

        GuestSectionTitle("Cuenta")
        SettingsPillRow(
            title = "Iniciar sesión",
            titleColor = RixyColors.Brand,
            onClick = onNavigateToLogin,
            showChevron = false
        )

        GuestSectionTitle("Acerca de")
        SettingsPillRow(
            title = "Versión",
            trailing = "1.0.0",
            onClick = null,
            showChevron = false
        )
    }
}

@Composable
private fun AuthenticatedProfileContent(
    userEmail: String?,
    selectedCityName: String?,
    languageLabel: String,
    currentModeLabel: String,
    canUseOwnerMode: Boolean,
    onModeChanged: () -> Unit,
    onSignOut: () -> Unit,
    onLanguageClick: (() -> Unit)?,
    onChangeCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ProfileHeaderSection(
            name = userEmail ?: "Usuario",
            email = if (canUseOwnerMode) "Propietario" else "Usuario"
        )

        GuestSectionTitle("Idioma")
        SettingsPillRow(
            title = "Idioma",
            trailing = languageLabel,
            titleColor = RixyColors.Brand,
            onClick = onLanguageClick,
            showChevron = onLanguageClick != null
        )

        if (canUseOwnerMode || userEmail != null) {
            GuestSectionTitle("Modo de Vista")
            SettingsPillRow(
                title = "Vista actual",
                trailing = currentModeLabel,
                titleColor = RixyColors.Brand,
                onClick = if (canUseOwnerMode) onModeChanged else null,
                showChevron = canUseOwnerMode
            )
        }

        GuestSectionTitle("Ciudad seleccionada")
        SettingsPillRow(
            title = selectedCityName ?: "Seleccionar ciudad",
            trailing = if (selectedCityName.isNullOrBlank()) "" else "Cambiar",
            trailingColor = RixyColors.Brand,
            onClick = onChangeCityClick,
            showChevron = false
        )

        GuestSectionTitle("Cuenta")
        SettingsPillRow(
            title = "Cerrar sesión",
            titleColor = RixyColors.Brand,
            onClick = onSignOut,
            showChevron = false
        )

        GuestSectionTitle("Acerca de")
        SettingsPillRow(
            title = "Versión",
            trailing = "1.0.0",
            onClick = null,
            showChevron = false
        )
    }
}

@Composable
private fun GuestSectionTitle(title: String) {
    Text(
        text = title,
        style = RixyTypography.H4,
        color = RixyColors.TextSecondary,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingsPillRow(
    title: String,
    trailing: String = "",
    titleColor: Color = RixyColors.TextPrimary,
    trailingColor: Color = RixyColors.TextSecondary,
    showChevron: Boolean = true,
    onClick: (() -> Unit)?
) {
    Surface(
        onClick = { onClick?.invoke() },
        shape = RoundedCornerShape(28.dp),
        color = RixyColors.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = RixyTypography.H4,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )

            if (trailing.isNotBlank()) {
                Text(
                    text = trailing,
                    style = RixyTypography.H4,
                    color = trailingColor
                )
            }

            if (showChevron) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = RixyColors.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = RixyTypography.CaptionSmall,
        color = RixyColors.TextTertiary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileHeaderSection(
    name: String,
    email: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = RixyColors.BrandLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = RixyColors.Brand,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = RixyTypography.Body,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = email,
                style = RixyTypography.BodySmall,
                color = RixyColors.TextSecondary
            )
        }

        Surface(
            shape = CircleShape,
            color = RixyColors.BrandLight
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = RixyColors.Brand,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

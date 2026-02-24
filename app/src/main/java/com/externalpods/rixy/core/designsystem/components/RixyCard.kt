package com.externalpods.rixy.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyShapes
import com.externalpods.rixy.core.designsystem.theme.RixySpacing

@Composable
fun RixyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RixyShapes.Card,
    backgroundColor: Color = RixyColors.Surface,
    contentColor: Color = RixyColors.TextPrimary,
    border: BorderStroke? = BorderStroke(1.dp, RixyColors.Border),
    elevation: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = backgroundColor,
        contentColor = contentColor
    )
    
    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = elevation
    )
    
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            elevation = cardElevation,
            border = border
        ) {
            Column(
                modifier = Modifier.padding(RixySpacing.CardPadding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            elevation = cardElevation,
            border = border
        ) {
            Column(
                modifier = Modifier.padding(RixySpacing.CardPadding),
                content = content
            )
        }
    }
}

@Composable
fun RixySurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RixyShapes.Card,
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    RixyCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        backgroundColor = MaterialTheme.colorScheme.surface,
        border = border,
        elevation = elevation,
        content = content
    )
}

@Composable
fun RixyOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RixyShapes.Card,
    content: @Composable ColumnScope.() -> Unit
) {
    RixyCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        elevation = 0.dp,
        border = BorderStroke(1.dp, RixyColors.Border),
        content = content
    )
}

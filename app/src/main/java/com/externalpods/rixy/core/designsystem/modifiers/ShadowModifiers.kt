package com.externalpods.rixy.core.designsystem.modifiers

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.externalpods.rixy.core.designsystem.theme.RixyColors
import com.externalpods.rixy.core.designsystem.theme.RixyShadows
import com.externalpods.rixy.core.designsystem.theme.ShadowStyle

/**
 * Applies an iOS-style soft shadow to the component.
 * Uses different implementations based on API level for best visual result.
 *
 * @param style The shadow style configuration
 * @param shape The shape of the shadow (should match the component shape)
 * @param clip Whether to clip the content to the shape
 */
fun Modifier.iosShadow(
    style: ShadowStyle = RixyShadows.Card,
    shape: Shape = RectangleShape,
    clip: Boolean = true
): Modifier = composed {
    val density = LocalDensity.current
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // API 31+: Use RenderEffect for true blur (best quality)
        this.then(
            Modifier
                .then(if (clip) Modifier.clip(shape) else Modifier)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            this.color = style.color
                            asFrameworkPaint().apply {
                                // Use BlurMaskFilter for soft shadow
                                maskFilter = BlurMaskFilter(
                                    with(density) { style.blurRadius.toPx() },
                                    BlurMaskFilter.Blur.NORMAL
                                )
                            }
                        }
                        
                        val shadowOffset = Offset(
                            with(density) { style.offsetX.toPx() },
                            with(density) { style.offsetY.toPx() }
                        )
                        
                        canvas.save()
                        canvas.translate(shadowOffset.x, shadowOffset.y)
                        
                        // Draw shadow based on shape
                        when (shape) {
                            is androidx.compose.foundation.shape.RoundedCornerShape -> {
                                // Extract corner radius - simplified for common cases
                                val radius = 16.dp // Default card radius
                                canvas.drawRoundRect(
                                    left = 0f,
                                    top = 0f,
                                    right = size.width,
                                    bottom = size.height,
                                    radiusX = with(density) { radius.toPx() },
                                    radiusY = with(density) { radius.toPx() },
                                    paint = paint
                                )
                            }
                            else -> {
                                canvas.drawRect(
                                    left = 0f,
                                    top = 0f,
                                    right = size.width,
                                    bottom = size.height,
                                    paint = paint
                                )
                            }
                        }
                        
                        canvas.restore()
                    }
                }
        )
    } else {
        // API < 31: Use layered shadows to simulate blur
        this.then(
            Modifier
                .then(if (clip) Modifier.clip(shape) else Modifier)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        // Multiple shadow layers for soft effect
                        val layers = 3
                        val baseAlpha = style.color.alpha / layers
                        
                        for (i in 0 until layers) {
                            val layerBlur = style.blurRadius * (1f + i * 0.3f)
                            val layerAlpha = baseAlpha * (1f - i * 0.2f)
                            
                            val paint = Paint().apply {
                                color = style.color.copy(alpha = layerAlpha)
                                asFrameworkPaint().apply {
                                    maskFilter = BlurMaskFilter(
                                        with(density) { layerBlur.toPx() },
                                        BlurMaskFilter.Blur.NORMAL
                                    )
                                }
                            }
                            
                            val shadowOffset = Offset(
                                with(density) { style.offsetX.toPx() },
                                with(density) { style.offsetY.toPx() } * (1f + i * 0.1f)
                            )
                            
                            canvas.save()
                            canvas.translate(shadowOffset.x, shadowOffset.y)
                            
                            canvas.drawRect(
                                left = 0f,
                                top = 0f,
                                right = size.width,
                                bottom = size.height,
                                paint = paint
                            )
                            
                            canvas.restore()
                        }
                    }
                }
        )
    }
}

/**
 * Simplified shadow modifier for cards with default iOS styling
 */
fun Modifier.cardShadow(
    elevation: Dp = 6.dp,
    borderRadius: Dp = 16.dp
): Modifier = composed {
    iosShadow(
        style = RixyShadows.Card,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(borderRadius),
        clip = true
    )
}

/**
 * Shadow modifier for modal/bottom sheet components
 */
fun Modifier.modalShadow(
    borderRadius: Dp = 20.dp
): Modifier = composed {
    iosShadow(
        style = RixyShadows.Modal,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(borderRadius),
        clip = true
    )
}

/**
 * Focus glow modifier for input fields
 */
fun Modifier.focusGlow(
    isFocused: Boolean,
    color: Color = RixyColors.Brand,
    borderRadius: Dp = 8.dp
): Modifier = composed {
    if (!isFocused) return@composed this
    
    iosShadow(
        style = RixyShadows.focusGlow(color),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(borderRadius),
        clip = false
    )
}

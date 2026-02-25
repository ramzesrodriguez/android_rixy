package com.externalpods.rixy.core.designsystem.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.externalpods.rixy.core.network.ApiConfig
import com.externalpods.rixy.core.designsystem.theme.RixyColors

private const val TAG = "DSAsyncImage"
private val API_ORIGIN: String by lazy {
    ApiConfig.BASE_URL
        .substringBefore("/api/")
        .trimEnd('/')
}

/**
 * DSAsyncImage - Unified image component that handles both URLs and Base64
 * 
 * Supports:
 * - HTTP/HTTPS URLs (using Coil)
 * - Base64 encoded images (data:image/png;base64,...)
 * - Placeholder while loading
 * - Error fallback
 */
@Composable
fun DSAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    val resolvedImageUrl = resolveRemoteImageUrl(imageUrl)
    Log.d(TAG, "Loading image: ${resolvedImageUrl?.take(100)}...")
    
    if (resolvedImageUrl.isNullOrBlank()) {
        Log.w(TAG, "Image URL is null or blank")
        EmptyImageState(modifier = modifier)
        return
    }

    // Check if it's a Base64 image
    if (resolvedImageUrl.startsWith("data:image")) {
        Log.d(TAG, "Detected Base64 image")
        Base64Image(
            base64String = resolvedImageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            placeholder = placeholder,
            error = error
        )
    } else {
        // Regular URL - use Coil
        Log.d(TAG, "Detected URL image: $resolvedImageUrl")
        UrlImage(
            imageUrl = resolvedImageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            placeholder = placeholder,
            error = error
        )
    }
}

@Composable
private fun UrlImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    val context = LocalContext.current
    
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .listener(
                onStart = { Log.d(TAG, "Coil: Started loading $imageUrl") },
                onSuccess = { _, result -> 
                    Log.d(TAG, "Coil: Success loading image, source: ${result.dataSource}")
                },
                onError = { _, result -> 
                    Log.e(TAG, "Coil: Error loading image: ${result.throwable.message}", result.throwable)
                }
            )
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RixyColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (placeholder != null) {
                    Image(
                        painter = placeholder,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = RixyColors.Brand,
                        strokeWidth = 2.dp
                    )
                }
            }
        },
        error = {
            Log.e(TAG, "Coil: Error state triggered")
            ErrorImageState(modifier = modifier, error = error)
        }
    )
}

fun resolveRemoteImageUrl(raw: String?): String? {
    if (raw == null) return null
    val value = raw.trim()
    if (value.isBlank()) return value
    if (value.startsWith("data:image")) return value
    if (value.startsWith("//")) return "https:$value"
    if (value.startsWith("http://") || value.startsWith("https://")) {
        val uri = Uri.parse(value)
        val host = uri.host?.lowercase().orEmpty()
        if (host == "localhost" || host == "127.0.0.1" || host == "0.0.0.0" || host == "::1") {
            val apiUri = Uri.parse(API_ORIGIN)
            val targetHost = apiUri.host ?: host
            val targetScheme = apiUri.scheme ?: uri.scheme ?: "http"
            val targetPort = if (apiUri.port != -1) apiUri.port else uri.port
            val targetAuthority = if (targetPort != -1) "$targetHost:$targetPort" else targetHost

            val rewritten = uri.buildUpon()
                .scheme(targetScheme)
                .encodedAuthority(targetAuthority)
                .build()
                .toString()
            Log.w(TAG, "Rewrote localhost image URL: $value -> $rewritten")
            return rewritten
        }
        return value
    }
    if (value.startsWith("/")) return "$API_ORIGIN$value"
    return "$API_ORIGIN/${value.removePrefix("/")}"
}

/**
 * Base64Image - Decodes and displays Base64 encoded images
 */
@Composable
private fun Base64Image(
    base64String: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    val bitmap = remember(base64String) {
        Log.d(TAG, "Decoding Base64 image, length: ${base64String.length}")
        decodeBase64ToBitmap(base64String)
    }

    if (bitmap != null) {
        Log.d(TAG, "Base64 decoded successfully: ${bitmap.width}x${bitmap.height}")
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Log.e(TAG, "Failed to decode Base64 image")
        ErrorImageState(modifier = modifier, error = error)
    }
}

@Composable
private fun EmptyImageState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(RixyColors.SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = null,
            tint = RixyColors.TextTertiary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun ErrorImageState(modifier: Modifier = Modifier, error: Painter? = null) {
    Box(
        modifier = modifier
            .background(RixyColors.SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (error != null) {
            Image(
                painter = error,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                tint = RixyColors.Error,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Decodes a Base64 image string to Bitmap
 * Supports formats: data:image/png;base64,xxx or data:image/jpeg;base64,xxx
 */
private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        // Remove the data URI scheme if present
        val base64Data = when {
            base64String.contains(",") -> base64String.substringAfter(",")
            else -> base64String
        }
        
        Log.d(TAG, "Base64 data length after cleaning: ${base64Data.length}")
        
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        Log.d(TAG, "Decoded bytes: ${bytes.size}")
        
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).also {
            if (it == null) {
                Log.e(TAG, "BitmapFactory returned null")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error decoding Base64: ${e.message}", e)
        null
    }
}

/**
 * DSAsyncImagePainter - Returns a painter for use in other components
 * Useful when you need a Painter instead of a composable
 */
@Composable
fun rememberAsyncImagePainter(imageUrl: String?): Painter? {
    if (imageUrl.isNullOrBlank()) return null
    
    // For Base64 images
    if (imageUrl.startsWith("data:image")) {
        val bitmap = remember(imageUrl) {
            decodeBase64ToBitmap(imageUrl)
        }
        return bitmap?.let { BitmapPainter(it.asImageBitmap()) }
    }
    
    // For regular URLs, Coil's painter should be used directly in AsyncImage
    return null
}

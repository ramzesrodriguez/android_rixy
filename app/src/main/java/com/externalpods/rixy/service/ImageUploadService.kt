package com.externalpods.rixy.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.externalpods.rixy.core.common.ApiError
import com.externalpods.rixy.data.repository.OwnerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

/**
 * Sealed class representing upload errors
 */
sealed class UploadError : Exception() {
    data class InvalidImage(override val message: String = "Imagen inválida") : UploadError()
    data class InvalidResponse(override val message: String = "Respuesta inválida del servidor") : UploadError()
    data class UploadFailed(override val message: String = "Error al subir la imagen") : UploadError()
    data class CompressionFailed(override val message: String = "Error al comprimir la imagen") : UploadError()
    data class NetworkError(override val cause: Throwable) : UploadError() {
        override val message: String = "Error de red: ${cause.message ?: "Unknown error"}"
    }
}

/**
 * Result of image compression
 */
data class CompressedImage(
    val bytes: ByteArray,
    val width: Int,
    val height: Int,
    val originalSize: Long,
    val compressedSize: Long
) {
    val compressionRatio: Float get() = if (originalSize > 0) compressedSize.toFloat() / originalSize else 1f
}

class ImageUploadService(
    private val ownerRepository: OwnerRepository,
    private val okHttpClient: OkHttpClient,
    private val context: Context
) {
    companion object {
        const val DEFAULT_COMPRESSION_QUALITY = 80
        const val MAX_WIDTH = 2048
        const val MAX_HEIGHT = 2048
        const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L // 5MB
    }

    /**
     * Compress and upload a single image
     * @param uri The image URI
     * @param filename Optional filename (auto-generated if null)
     * @param quality JPEG compression quality (0-100), default 80
     * @return Result containing the public URL or UploadError
     */
    suspend fun uploadImage(
        uri: Uri, 
        filename: String? = null,
        quality: Int = DEFAULT_COMPRESSION_QUALITY
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Compress image
                val compressed = compressImage(uri, quality)
                    ?: return@withContext Result.failure(UploadError.CompressionFailed())

                // 2. Generate filename if not provided
                val actualFilename = filename ?: "${UUID.randomUUID()}.jpg"

                // 3. Get presigned URL
                val presignData = ownerRepository.presignUpload(actualFilename, "image/jpeg")

                // 4. Upload to presigned URL
                val request = Request.Builder()
                    .url(presignData.uploadUrl)
                    .put(compressed.bytes.toRequestBody("image/jpeg".toMediaType()))
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    Result.success(presignData.publicUrl)
                } else {
                    Result.failure(UploadError.UploadFailed("Upload failed: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(when (e) {
                    is UploadError -> e
                    is IOException -> UploadError.NetworkError(e)
                    else -> UploadError.UploadFailed(e.message ?: "Unknown error")
                })
            }
        }
    }

    /**
     * Upload multiple images
     * @param uris List of image URIs
     * @param quality JPEG compression quality
     * @return List of Results (success or failure for each image)
     */
    suspend fun uploadImages(
        uris: List<Uri>, 
        quality: Int = DEFAULT_COMPRESSION_QUALITY
    ): List<Result<String>> {
        return uris.map { uri ->
            uploadImage(uri, quality = quality)
        }
    }

    /**
     * Compress image maintaining aspect ratio
     * @param uri Source image URI
     * @param quality JPEG compression quality
     * @return CompressedImage or null if failed
     */
    fun compressImage(uri: Uri, quality: Int = DEFAULT_COMPRESSION_QUALITY): CompressedImage? {
        return try {
            // Get original dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            val originalWidth = options.outWidth
            val originalHeight = options.outHeight

            // Calculate sample size to reduce memory usage
            var sampleSize = 1
            while (originalWidth / sampleSize > MAX_WIDTH || originalHeight / sampleSize > MAX_HEIGHT) {
                sampleSize *= 2
            }

            // Decode with sample size
            val bitmapOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, bitmapOptions)
            } ?: return null

            // Scale down if still too large
            val scaledBitmap = if (bitmap.width > MAX_WIDTH || bitmap.height > MAX_HEIGHT) {
                val ratio = minOf(
                    MAX_WIDTH.toFloat() / bitmap.width,
                    MAX_HEIGHT.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true).also {
                    if (it != bitmap) bitmap.recycle()
                }
            } else {
                bitmap
            }

            // Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            val success = scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            
            if (!success) {
                scaledBitmap.recycle()
                return null
            }

            val compressedBytes = outputStream.toByteArray()
            scaledBitmap.recycle()

            // Get original file size
            val originalSize = context.contentResolver.openFileDescriptor(uri, "r")?.use {
                it.statSize
            } ?: 0L

            CompressedImage(
                bytes = compressedBytes,
                width = scaledBitmap.width,
                height = scaledBitmap.height,
                originalSize = originalSize,
                compressedSize = compressedBytes.size.toLong()
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get image dimensions without loading full bitmap
     */
    fun getImageDimensions(uri: Uri): Pair<Int, Int>? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            Pair(options.outWidth, options.outHeight)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if image needs compression (larger than max size)
     */
    fun needsCompression(uri: Uri, maxSizeBytes: Long = MAX_FILE_SIZE_BYTES): Boolean {
        val size = context.contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: return false
        return size > maxSizeBytes
    }
}

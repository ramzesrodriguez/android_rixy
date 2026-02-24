package com.externalpods.rixy.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {

    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val iso8601FormatNoMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
    private val displayFormatWithTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX"))

    fun parseIso8601(dateString: String?): Date? {
        if (dateString.isNullOrBlank()) return null
        return try {
            iso8601Format.parse(dateString)
        } catch (_: Exception) {
            try {
                iso8601FormatNoMillis.parse(dateString)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun formatDisplay(dateString: String?): String {
        val date = parseIso8601(dateString) ?: return ""
        return displayFormat.format(date)
    }

    fun formatDisplayWithTime(dateString: String?): String {
        val date = parseIso8601(dateString) ?: return ""
        return displayFormatWithTime.format(date)
    }

    fun relativeTime(dateString: String?): String {
        val date = parseIso8601(dateString) ?: return ""
        val now = Date()
        val diffMs = now.time - date.time

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        val hours = TimeUnit.MILLISECONDS.toHours(diffMs)
        val days = TimeUnit.MILLISECONDS.toDays(diffMs)

        return when {
            minutes < 1 -> "Ahora"
            minutes < 60 -> "Hace ${minutes}m"
            hours < 24 -> "Hace ${hours}h"
            days < 7 -> "Hace ${days}d"
            days < 30 -> "Hace ${days / 7}sem"
            else -> formatDisplay(dateString)
        }
    }
}

package com.externalpods.rixy.core.common

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {

    private val mxnFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX")).apply {
        currency = Currency.getInstance("MXN")
    }

    fun format(amount: String?, currency: String? = "MXN"): String {
        if (amount.isNullOrBlank()) return ""
        val value = amount.toDoubleOrNull() ?: return amount
        return when (currency?.uppercase()) {
            "MXN" -> mxnFormat.format(value)
            "USD" -> NumberFormat.getCurrencyInstance(Locale.US).format(value)
            else -> "$${String.format("%.2f", value)} ${currency ?: ""}"
        }
    }

    fun formatCents(amountCents: Int?, currency: String? = "MXN"): String {
        if (amountCents == null) return ""
        val amount = amountCents / 100.0
        return format(amount.toString(), currency)
    }
}

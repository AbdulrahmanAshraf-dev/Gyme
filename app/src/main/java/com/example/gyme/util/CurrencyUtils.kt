package com.example.gyme.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    /**
     * Formats a double amount into Egyptian Pound (EGP) format.
     * Uses 'EGP' for English and 'ج.م' for Arabic based on the current locale.
     */
    fun formatEGP(amount: Double): String {
        // We use Arabic Egypt locale for the formatting to get 'ج.م' symbol
        // If the system language is English, we can customize it to show 'EGP'
        val locale = Locale.getDefault()
        return if (locale.language == "ar") {
            val format = NumberFormat.getCurrencyInstance(Locale("ar", "EG"))
            format.format(amount)
        } else {
            // Manual formatting for 'EGP' to look modern
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            val symbol = "EGP"
            "${format.format(amount).replace("$", "")} $symbol"
        }
    }
}

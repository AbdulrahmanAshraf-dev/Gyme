package com.example.gyme.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {

    fun formatEGP(amount: Double): String {

        val locale = Locale.getDefault()
        return if (locale.language == "ar") {
            val format = NumberFormat.getCurrencyInstance(Locale("ar", "EG"))
            format.format(amount)
        } else {
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            val symbol = "EGP"
            "${format.format(amount).replace("$", "")} $symbol"
        }
    }
}

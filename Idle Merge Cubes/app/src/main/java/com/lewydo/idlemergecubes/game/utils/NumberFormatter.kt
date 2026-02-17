package com.lewydo.idlemergecubes.game.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {
    fun format(value: Long, separator: Char = ','): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = separator
        }

        val formatter = DecimalFormat("#,###", symbols)
        formatter.isGroupingUsed = true

        return formatter.format(value)
    }
}
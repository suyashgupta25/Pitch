package com.axioo.feed.ui.components

import java.util.Locale

fun Long.formatCompact(): String {
    val abs = kotlin.math.abs(this)
    return when {
        abs < 1_000L -> this.toString()
        abs < 1_000_000L -> trim(this / 1_000.0, "k")
        abs < 1_000_000_000L -> trim(this / 1_000_000.0, "M")
        else -> trim(this / 1_000_000_000.0, "B")
    }
}

private fun trim(
    value: Double,
    suffix: String,
): String {
    val formatted = String.format(Locale.US, "%.1f", value)
    val trimmed = if (formatted.endsWith(".0")) formatted.dropLast(2) else formatted
    return "$trimmed$suffix"
}

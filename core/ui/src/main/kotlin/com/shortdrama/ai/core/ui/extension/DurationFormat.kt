package com.shortdrama.ai.core.ui.extension

import java.util.Locale

/** 596 -> "9:56", 3725 -> "1:02:05". */
fun Long.formatDuration(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}

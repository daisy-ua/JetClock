package com.daisy.jetclock.presentation.utils.formatter

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    fun formatTriggerDate(triggerTime: Long): String {
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
            .withZone(ZoneId.systemDefault())

        return formatter.format(Instant.ofEpochMilli(triggerTime))
    }

    fun formatCurrentDate(): String {
        val timeInMillis = System.currentTimeMillis()
        return formatTriggerDate(timeInMillis)
    }
}
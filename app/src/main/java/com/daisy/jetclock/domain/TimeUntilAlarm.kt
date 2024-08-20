package com.daisy.jetclock.domain

import java.util.concurrent.TimeUnit

class TimeUntilAlarm(private val futureTimeMillis: Long) {

    fun getTimeUntil(): String? {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = futureTimeMillis - currentTimeMillis

        if (timeDifferenceMillis <= 0) return null

        val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60

        return displayString(days, hours, minutes)
    }

    private fun displayString(days: Long, hours: Long, minutes: Long): String {
        val timeParts = mutableListOf<String>()

        if (days > 0) timeParts.add(formatTimePart(days, "day"))
        if (hours > 0) timeParts.add(formatTimePart(hours, "hour"))
        if (minutes > 0) timeParts.add(formatTimePart(minutes, "minute"))

        return "Ring in " + if (timeParts.isEmpty()) {
            "less than a minute."
        } else {
            timeParts.joinToString(" ") + "."
        }
    }

    private fun formatTimePart(value: Long, unit: String): String {
        return "$value $unit${if (value > 1) "s" else ""}"
    }
}
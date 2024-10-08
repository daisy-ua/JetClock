package com.daisy.jetclock.utils

import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.TimeUntilAlarm

fun getTimeLeftUntilAlarm(timeInMillis: Long): String {
    return TimeUntilAlarm(timeInMillis).getTimeUntil() ?: "Error occurred."
}

fun getNextAlarmTime(alarms: List<Alarm>): Pair<Alarm, String>? {
    alarms
        .filter { it.triggerTime != null }
        .sortedBy { alarm -> alarm.triggerTime }
        .firstOrNull()
        ?.let { nextAlarm ->
            val timeString =
                TimeUntilAlarm(nextAlarm.triggerTime!!).getTimeUntil() ?: run { return null }

            return nextAlarm to timeString
        } ?: return null
}
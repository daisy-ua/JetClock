package com.daisy.jetclock.presentation.utils.helper

import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.utils.next.TimeUntilNextAlarm
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * A utility class for converting time in milliseconds to different time representations
 */
object TimeMillisUtils {

    fun convertToTimeOfDay(timeInMillis: Long): TimeOfDay {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val hour = calendar.get(Calendar.HOUR).run { if (this == 0) 12 else this }
        val minute = calendar.get(Calendar.MINUTE)
        val meridiem =
            if (calendar.get(Calendar.AM_PM) == Calendar.AM) MeridiemOption.AM else MeridiemOption.PM

        return TimeOfDay(hour, minute, meridiem)
    }

    fun convertTimeUntilAlarmGoesOff(timeInMillis: Long): TimeUntilNextAlarm? {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = timeInMillis - currentTimeMillis

        if (timeDifferenceMillis <= 0) return null

        val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60

        return TimeUntilNextAlarm(days, hours, minutes)
    }
}
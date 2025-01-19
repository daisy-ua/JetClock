package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.utils.helper.TimeMillisUtils
import com.daisy.jetclock.presentation.utils.helper.TimeMillisUtils.convertToTimeOfDay
import com.daisy.jetclock.presentation.utils.next.TimeUntilNextAlarm

object TimeFormatter {

    fun formatTime(context: Context, time: TimeOfDay): String {
        return context.getString(R.string.time_format, time.hour, time.minute, "")
    }

    fun formatTimeWithMeridiem(context: Context, time: TimeOfDay): String {
        val amPm = time.meridiem?.getLocalizedString(context) ?: ""
        return context.getString(R.string.time_format, time.hour, time.minute, amPm)
    }

    fun formatTimeWithMeridiem(context: Context, timeInMillis: Long): String {
        val timeOfDay = convertToTimeOfDay(timeInMillis)

        return formatTimeWithMeridiem(context, timeOfDay)
    }

    fun formatTimeUntilAlarmGoesOff(
        context: Context,
        timeUntilNextAlarm: TimeUntilNextAlarm,
    ): String {
        val (days, hours, minutes) = timeUntilNextAlarm

        val timeParts = mutableListOf<String>()

        if (days > 0) timeParts.add(formatTimePart(context, days, R.plurals.time_part_day))
        if (hours > 0) timeParts.add(formatTimePart(context, hours, R.plurals.time_part_hour))
        if (minutes > 0) timeParts.add(formatTimePart(context, minutes, R.plurals.time_part_minute))

        return if (timeParts.isEmpty()) {
            context.getString(R.string.time_until_alarm_less_than_a_minute)
        } else {
            val formattedTimeParts = timeParts.joinToString(" ")
            context.getString(R.string.time_until_alarm, formattedTimeParts)
        }
    }

    fun formatTimeUntilAlarmGoesOff(
        context: Context,
        timeInMillis: Long?,
    ): String {
        val timeUntilNextAlarm =
            timeInMillis?.let { time -> TimeMillisUtils.convertTimeUntilAlarmGoesOff((time)) }
                ?: throw Exception("Error occurred while parsing time.")

        return formatTimeUntilAlarmGoesOff(context, timeUntilNextAlarm)
    }

    private fun formatTimePart(context: Context, value: Long, pluralResourceId: Int): String {
        return context.resources.getQuantityString(pluralResourceId, value.toInt(), value)
    }
}
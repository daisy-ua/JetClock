package com.daisy.jetclock.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.DayOfWeek

object AlarmFormatter {
    fun getTimeString(context: Context, hour: Int, minute: Int): String {
        val formattedMinute = TimeFormatter.convertTwoCharTime(minute)
        return context.getString(R.string.time_format, hour, formattedMinute)
    }

    fun getRepeatDaysString(context: Context, repeatDays: List<DayOfWeek>): String {
        return when {
            repeatDays.isEmpty() -> context.getString(R.string.ring_once)

            repeatDays.size == 7 -> context.getString(R.string.everyday)

            repeatDays.size == 5 && !repeatDays.containsAll(
                listOf(
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                )
            ) -> context.getString(R.string.monday_to_friday)

            else -> repeatDays.joinToString { day -> day.abbr }
        }
    }
}
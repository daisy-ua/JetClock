package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.domain.model.TimeOfDay
import java.util.Calendar

object TimeFormatter {

    fun formatTime(context: Context, time: TimeOfDay): String {
        return context.getString(R.string.time_format, time.hour, time.minute, "")
    }

    fun formatTimeWithMeridiem(context: Context, time: TimeOfDay): String {
        val amPm = time.meridiem?.getLocalizedString(context) ?: ""
        return context.getString(R.string.time_format, time.hour, time.minute, amPm)
    }

    fun formatTimeWithMeridiem(context: Context, timeInMillis: Long): String {
        val timeOfDay = convertTimeInMillis(timeInMillis)

        return formatTimeWithMeridiem(context, timeOfDay)
    }

    fun convertTimeInMillis(timeInMillis: Long): TimeOfDay {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val meridiem =
            if (calendar.get(Calendar.AM_PM) == Calendar.AM) MeridiemOption.AM else MeridiemOption.PM

        return TimeOfDay(hour, minute, meridiem)
    }
}